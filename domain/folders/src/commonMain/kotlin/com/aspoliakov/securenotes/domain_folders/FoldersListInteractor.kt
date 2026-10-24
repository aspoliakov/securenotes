package com.aspoliakov.securenotes.domain_folders

import com.aspoliakov.securenotes.core_db.dao.FolderDao
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.model.FolderDB
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.domain_folders.model.FolderDTO
import com.aspoliakov.securenotes.domain_folders.model.FolderVO
import com.aspoliakov.securenotes.domain_user_state.model.NotesSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * Project SecureNotes
 */

class FoldersListInteractor(
        private val folderDao: FolderDao,
        private val syncStackDao: SyncStackDao,
        private val folderCryptoInteractor: FolderCryptoInteractor,
) {

    fun getChildFolders(parentId: String?, sortOrder: NotesSortOrder): Flow<List<FolderVO>> {
        val foldersFlow = when (sortOrder) {
            NotesSortOrder.NEWEST_FIRST -> folderDao.selectChildrenByParentIdOrderByCreatedAtDesc(parentId)
            NotesSortOrder.OLDEST_FIRST -> folderDao.selectChildrenByParentIdOrderByCreatedAtAsc(parentId)
            NotesSortOrder.CUSTOM -> folderDao.selectChildrenByParentIdOrderByOrderAsc(parentId)
        }
        return foldersFlow
            .map { folders -> folders.map(this::mapFolderDBToFolderVO) }
    }

    suspend fun searchFolders(query: String): List<FolderVO> {
        return folderDao.searchByName(query)
            .map(this::mapFolderDBToFolderVO)
    }

    /**
     * Applies folders changed on the server. A deleted one is removed whatever its local state: the deletion
     * wins over local changes (the server deletes its contents too and sends them as well). Others replace
     * the local ones unless those have changes not uploaded yet (the last upload wins).
     */
    suspend fun applyServerChanges(folders: List<FolderDTO>) {
        val (deletedFolders, changedFolders) = folders.partition { it.deletedAt != null }
        deletedFolders.forEach {
            folderDao.delete(it.folderId)
            syncStackDao.delete(it.folderId)
        }
        val pendingIds = syncStackDao.selectIdsByItemType(SyncStackDB.ItemType.FOLDER).toSet()
        val changedFoldersDB = changedFolders
            .filterNot { it.folderId in pendingIds }
            .mapNotNull { folderDTO ->
                val payload = folderDTO.payload ?: return@mapNotNull null
                val folderPayload = folderCryptoInteractor.decrypt(payload)
                FolderDB(
                        folderId = folderDTO.folderId,
                        parentId = folderDTO.parentId,
                        createdAt = folderDTO.createdAt.toEpochMillis(),
                        name = folderPayload.name,
                        order = folderDTO.order,
                )
            }
        folderDao.insertOrReplace(changedFoldersDB)
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
}

private fun String.toEpochMillis(): Long {
    return LocalDateTime.parse(this).toInstant(TimeZone.UTC).toEpochMilliseconds()
}
