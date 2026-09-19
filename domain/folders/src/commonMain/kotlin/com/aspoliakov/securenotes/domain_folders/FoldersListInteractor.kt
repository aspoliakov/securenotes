package com.aspoliakov.securenotes.domain_folders

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_db.dao.FolderDao
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.model.FolderDB
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.domain_folders.model.FolderVO
import com.aspoliakov.securenotes.domain_folders.network.FoldersApiProvider
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import com.aspoliakov.securenotes.domain_user_state.model.NotesSortOrder
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * Project SecureNotes
 */

class FoldersListInteractor(
        private val folderDao: FolderDao,
        private val syncStackDao: SyncStackDao,
        private val foldersApiProvider: FoldersApiProvider,
        private val userStateInteractor: UserStateInteractor,
        private val folderCryptoInteractor: FolderCryptoInteractor,
) {

    fun getChildFolders(parentId: String?, sortOrder: NotesSortOrder): Flow<List<FolderVO>> {
        val foldersFlow = when (sortOrder) {
            NotesSortOrder.NEWEST_FIRST -> folderDao.selectChildrenByParentIdOrderByCreatedAtDesc(parentId)
            NotesSortOrder.OLDEST_FIRST -> folderDao.selectChildrenByParentIdOrderByCreatedAtAsc(parentId)
        }
        return foldersFlow
            .map { folders -> folders.map(this::mapFolderDBToFolderVO) }
            .also { IOScope().launch { syncFolders() } }
    }

    suspend fun searchFolders(query: String): List<FolderVO> {
        return folderDao.searchByName(query)
            .map(this::mapFolderDBToFolderVO)
    }

    suspend fun syncFolders() {
        runCatching {
            // folders with a pending sync-stack entry are not yet reflected on the server (or are
            // queued for local deletion); overwriting them here with stale server data would
            // resurrect a locally deleted folder or silently discard an unsynced local edit, so
            // they are left untouched until their own pending sync completes
            val pendingIds = syncStackDao.selectIdsByItemType(SyncStackDB.ItemType.FOLDER).toSet()
            val folders = foldersApiProvider.provideApi().getAllFolders(
                    token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
            )
                .folders
                .filterNot { it.folderId in pendingIds }
                .map {
                    val folderPayload = folderCryptoInteractor.decrypt(it.payload)
                    FolderDB(
                            folderId = it.folderId,
                            parentId = it.parentId,
                            createdAt = it.createdAt.toEpochMillis(),
                            name = folderPayload.name,
                    )
                }
            folderDao.insertOrReplace(folders)
        }
            .onFailure {
                Napier.e("Error syncing folders: $it")
            }
    }

    private fun mapFolderDBToFolderVO(folderDB: FolderDB): FolderVO {
        return FolderVO(
                id = folderDB.folderId,
                parentId = folderDB.parentId,
                createdAt = folderDB.createdAt,
                name = folderDB.name ?: "",
        )
    }
}

private fun String.toEpochMillis(): Long {
    return LocalDateTime.parse(this).toInstant(TimeZone.UTC).toEpochMilliseconds()
}
