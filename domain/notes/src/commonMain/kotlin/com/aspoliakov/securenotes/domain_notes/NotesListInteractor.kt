package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.model.NoteDB
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.domain_notes.model.NoteColor
import com.aspoliakov.securenotes.domain_notes.model.NoteDTO
import com.aspoliakov.securenotes.domain_notes.model.NoteVO
import com.aspoliakov.securenotes.domain_notes.util.toEpochMillis
import com.aspoliakov.securenotes.domain_user_state.model.NotesSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Project SecureNotes
 */

class NotesListInteractor(
        private val notesDao: NotesDao,
        private val syncStackDao: SyncStackDao,
        private val noteCryptoInteractor: NoteCryptoInteractor,
) {

    fun getNotes(folderId: String?, sortOrder: NotesSortOrder): Flow<List<NoteVO>> {
        val notesFlow = when (sortOrder) {
            NotesSortOrder.NEWEST_FIRST -> notesDao.selectByFolderIdOrderByCreatedAtDesc(folderId)
            NotesSortOrder.OLDEST_FIRST -> notesDao.selectByFolderIdOrderByCreatedAtAsc(folderId)
            NotesSortOrder.CUSTOM -> notesDao.selectByFolderIdOrderByOrderAsc(folderId)
        }
        return notesFlow
            .map { notesList -> notesList.map(this::mapNoteDBToNoteVO) }
    }

    suspend fun searchNotes(query: String): List<NoteVO> {
        return notesDao.searchAllByCreatedAtDesc(query)
            .map(this::mapNoteDBToNoteVO)
    }

    private fun mapNoteDBToNoteVO(noteDB: NoteDB): NoteVO {
        return NoteVO(
                id = noteDB.noteId,
                createdAt = noteDB.createdAt,
                contentUpdatedAt = noteDB.contentUpdatedAt,
                folderId = noteDB.folderId,
                title = noteDB.title ?: "",
                body = noteDB.body ?: "",
                color = NoteColor.fromArgb(noteDB.color),
                hidden = noteDB.hidden,
                order = noteDB.order,
        )
    }

    /**
     * Applies notes changed on the server. A deleted one is removed whatever its local state: the deletion
     * wins over local changes. Others replace the local ones unless those have changes not uploaded yet
     * (the last upload wins).
     */
    suspend fun applyServerChanges(notes: List<NoteDTO>) {
        val (deletedNotes, changedNotes) = notes.partition { it.deletedAt != null }
        deletedNotes.forEach {
            notesDao.delete(it.noteId)
            syncStackDao.delete(it.noteId)
        }
        val pendingIds = syncStackDao.selectIdsByItemType(SyncStackDB.ItemType.NOTE).toSet()
        val changedNotesDB = changedNotes
            .filterNot { it.noteId in pendingIds }
            .mapNotNull { noteDTO ->
                val payload = noteDTO.payload ?: return@mapNotNull null
                val notePayload = noteCryptoInteractor.decrypt(payload)
                NoteDB(
                        noteId = noteDTO.noteId,
                        folderId = noteDTO.folderId,
                        createdAt = noteDTO.createdAt.toEpochMillis(),
                        contentUpdatedAt = noteDTO.contentUpdatedAt.toEpochMillis(),
                        title = notePayload.title,
                        body = notePayload.body,
                        color = NoteColor.fromArgb(notePayload.color).argb,
                        hidden = notePayload.hidden,
                        order = noteDTO.order,
                )
            }
        notesDao.insertOrReplace(changedNotesDB)
    }
}
