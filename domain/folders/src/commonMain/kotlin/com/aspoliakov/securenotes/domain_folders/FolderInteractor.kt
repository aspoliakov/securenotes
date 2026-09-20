package com.aspoliakov.securenotes.domain_folders

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_base.util.randomUUIDString
import com.aspoliakov.securenotes.core_db.dao.FolderDao
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.event_bus.SyncStackEventBus
import com.aspoliakov.securenotes.core_db.model.FolderDB
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.domain_folders.model.FolderPayload
import com.aspoliakov.securenotes.domain_folders.model.FolderVO
import com.aspoliakov.securenotes.domain_folders.model.PostFolderRequest
import com.aspoliakov.securenotes.domain_folders.network.FoldersApiProvider
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import io.github.aakira.napier.Napier
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Project SecureNotes
 */

class FolderInteractor(
        private val folderDao: FolderDao,
        private val notesDao: NotesDao,
        private val syncStackDao: SyncStackDao,
        private val syncStackEventBus: SyncStackEventBus,
        private val foldersApiProvider: FoldersApiProvider,
        private val userStateInteractor: UserStateInteractor,
        private val folderCryptoInteractor: FolderCryptoInteractor,
) {

    companion object {
        private const val HANDLE_CHANGES_DELAY = 2000L
        private const val ORDER_STEP = 1000.0
    }

    private val syncJobs: MutableMap<String, Job> = mutableMapOf()

    @OptIn(ExperimentalTime::class)
    suspend fun createNew(parentId: String?, name: String): String {
        val maxOrder = maxOf(
                notesDao.selectMaxOrderByFolderId(parentId) ?: 0.0,
                folderDao.selectMaxOrderByParentId(parentId) ?: 0.0,
        )
        val newFolderDB = FolderDB(
                folderId = randomUUIDString(),
                parentId = parentId,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                name = name,
                order = maxOrder + ORDER_STEP,
        )
        folderDao.insertOrReplace(newFolderDB)
        addChangesToSyncStack(newFolderDB.folderId, SyncStackDB.Action.SAVE)
        return newFolderDB.folderId
    }

    fun rename(folderId: String, name: String) = IOScope().launch {
        folderDao.updateName(folderId, name)
        addChangesToSyncStack(folderId, SyncStackDB.Action.SAVE)
    }

    suspend fun reorder(folderId: String, order: Double) {
        folderDao.updateOrder(folderId, order)
        scheduleSync(folderId)
    }

    suspend fun delete(folderId: String) {
        syncJobs[folderId]?.cancel()
        val descendantFolderIds = collectDescendantIds(folderId)
        val affectedFolderIds = descendantFolderIds + folderId
        affectedFolderIds.forEach { id ->
            notesDao.selectIdsByFolderId(id).forEach { noteId ->
                syncStackDao.delete(noteId)
            }
            notesDao.deleteByFolderId(id)
        }
        descendantFolderIds.forEach { id ->
            folderDao.delete(id)
            syncStackDao.delete(id)
        }
        folderDao.delete(folderId)
        // the server cascades folder deletion to descendant folders and their notes within
        // delete_existing_folder, so only the top-level folderId needs to be synced to the
        // server
        addChangesToSyncStack(folderId, SyncStackDB.Action.DELETE)
    }

    suspend fun getFolder(folderId: String): FolderVO? {
        val folderDB = folderDao.selectById(folderId) ?: return null
        return mapFolderDBToFolderVO(folderDB)
    }

    suspend fun getFolderPath(folderId: String): List<FolderVO> {
        val path = mutableListOf<FolderVO>()
        var currentId: String? = folderId
        while (currentId != null) {
            val folderDB = folderDao.selectById(currentId) ?: break
            path.add(0, mapFolderDBToFolderVO(folderDB))
            currentId = folderDB.parentId
        }
        return path
    }

    suspend fun syncChanges(
            folderId: String,
            action: SyncStackDB.Action,
    ) {
        when (action) {
            SyncStackDB.Action.SAVE -> {
                val folderDB = folderDao.selectById(folderId) ?: return
                Napier.d("upload updated folder to server")
                val encryptedPayload = folderCryptoInteractor.encrypt(
                        FolderPayload(name = folderDB.name)
                )
                foldersApiProvider.provideApi().saveFolder(
                        token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
                        request = PostFolderRequest(
                                folderId = folderId,
                                parentId = folderDB.parentId,
                                keyId = encryptedPayload.keyId,
                                payload = encryptedPayload.payload,
                                order = folderDB.order,
                        )
                )
            }
            SyncStackDB.Action.DELETE -> {
                Napier.d("remove folder from server")
                foldersApiProvider.provideApi().deleteFolder(
                        token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
                        folderId = folderId,
                )
            }
        }
    }

    private suspend fun collectDescendantIds(folderId: String): List<String> {
        val result = mutableListOf<String>()
        var currentLevelIds = listOf(folderId)
        while (currentLevelIds.isNotEmpty()) {
            val nextLevelIds = currentLevelIds.flatMap { id ->
                folderDao.selectChildrenByParentIdOrderByCreatedAtDesc(id).first().map { it.folderId }
            }
            result += nextLevelIds
            currentLevelIds = nextLevelIds
        }
        return result
    }

    private fun mapFolderDBToFolderVO(folderDB: FolderDB): FolderVO {
        return FolderVO(
                id = folderDB.folderId,
                parentId = folderDB.parentId,
                createdAt = folderDB.createdAt,
                name = folderDB.name ?: "",
                order = folderDB.order,
        )
    }

    private fun scheduleSync(folderId: String) {
        syncJobs[folderId]?.cancel()
        syncJobs[folderId] = IOScope().launch {
            delay(HANDLE_CHANGES_DELAY.milliseconds)
            addChangesToSyncStack(folderId, SyncStackDB.Action.SAVE)
        }
    }

    private fun addChangesToSyncStack(
            folderId: String,
            action: SyncStackDB.Action,
    ) = IOScope().launch {
        val syncStackDB = SyncStackDB(
                itemId = folderId,
                itemType = SyncStackDB.ItemType.FOLDER,
                action = action,
        )
        syncStackDao.insertOrReplace(syncStackDB)
        syncStackEventBus.post(folderId)
    }
}
