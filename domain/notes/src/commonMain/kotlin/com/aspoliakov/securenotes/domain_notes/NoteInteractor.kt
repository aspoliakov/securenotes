package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_base.util.randomUUIDString
import com.aspoliakov.securenotes.core_db.dao.FolderDao
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.event_bus.SyncStackEventBus
import com.aspoliakov.securenotes.core_db.model.NoteDB
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.core_network.exceptions.Error404Exception
import com.aspoliakov.securenotes.domain_notes.model.NoteColor
import com.aspoliakov.securenotes.domain_notes.model.NotePayload
import com.aspoliakov.securenotes.domain_notes.model.NoteVO
import com.aspoliakov.securenotes.domain_notes.model.PostNoteRequest
import com.aspoliakov.securenotes.domain_notes.network.NotesApiProvider
import com.aspoliakov.securenotes.domain_notes.util.toEpochMillis
import com.aspoliakov.securenotes.domain_notes.util.toServerDateTime
import io.github.aakira.napier.Napier
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/**
 * Project SecureNotes
 */

class NoteInteractor(
        private val notesDao: NotesDao,
        private val folderDao: FolderDao,
        private val syncStackDao: SyncStackDao,
        private val syncStackEventBus: SyncStackEventBus,
        private val notesApiProvider: NotesApiProvider,
        private val noteCryptoInteractor: NoteCryptoInteractor,
) {

    companion object {
        private const val ORDER_STEP = 1000.0
    }

    suspend fun createNew(folderId: String? = null): String {
        val maxOrder = maxOf(
                notesDao.selectMaxOrderByFolderId(folderId) ?: 0.0,
                folderDao.selectMaxOrderByParentId(folderId) ?: 0.0,
        )
        val now = Clock.System.now().toEpochMilliseconds()
        val newNoteDB = NoteDB(
                noteId = randomUUIDString(),
                createdAt = now,
                contentUpdatedAt = now,
                folderId = folderId,
                order = maxOrder + ORDER_STEP,
        )
        notesDao.insertOrReplace(newNoteDB)
        return newNoteDB.noteId
    }

    suspend fun reorder(noteId: String, order: Double) {
        notesDao.updateOrder(noteId, order)
        addChangesToSyncStack(noteId, SyncStackDB.Action.SAVE)
        syncChanges(noteId)
    }

    suspend fun getById(noteId: String): NoteVO? {
        return notesDao.selectById(noteId)?.let { mapNoteDBToNoteVO(it) }
    }

    fun observeById(noteId: String): Flow<NoteVO> {
        return notesDao.observeById(noteId)
            .filterNotNull()
            .map { mapNoteDBToNoteVO(it) }
    }

    suspend fun undoCreation(noteId: String) {
        val noteDB = notesDao.selectById(noteId) ?: return
        if (noteDB.title == null && noteDB.body == null) {
            notesDao.delete(noteId)
            syncStackDao.delete(noteId)
        }
    }

    suspend fun syncChanges(noteId: String) {
        syncStackEventBus.post(noteId)
    }

    suspend fun setTitle(noteId: String, title: String) {
        val noteDB = notesDao.selectById(noteId) ?: return
        val newTitle = title.toStoredText()
        if (newTitle != noteDB.title) {
            notesDao.updateTitle(noteId, newTitle, Clock.System.now().toEpochMilliseconds())
            addChangesToSyncStack(noteId, SyncStackDB.Action.SAVE)
        }
    }

    suspend fun setBody(noteId: String, body: String) {
        val noteDB = notesDao.selectById(noteId) ?: return
        val newBody = body.toStoredText()
        if (newBody != noteDB.body) {
            notesDao.updateBody(noteId, newBody, Clock.System.now().toEpochMilliseconds())
            addChangesToSyncStack(noteId, SyncStackDB.Action.SAVE)
        }
    }

    suspend fun setColor(noteId: String, color: NoteColor) {
        val noteDB = notesDao.selectById(noteId) ?: return
        if (color != NoteColor.fromArgb(noteDB.color)) {
            notesDao.updateColor(noteId, color.argb, Clock.System.now().toEpochMilliseconds())
            addChangesToSyncStack(noteId, SyncStackDB.Action.SAVE)
        }
    }

    suspend fun delete(noteId: String) {
        notesDao.delete(noteId)
        addChangesToSyncStack(noteId, SyncStackDB.Action.DELETE)
        syncChanges(noteId)
    }

    suspend fun syncChanges(
            noteId: String,
            action: SyncStackDB.Action,
    ) {
        when (action) {
            SyncStackDB.Action.SAVE -> {
                val noteDB = notesDao.selectById(noteId) ?: return
                Napier.d("upload updated note to server")
                val encryptedPayload = noteCryptoInteractor.encrypt(
                        NotePayload(
                                title = noteDB.title,
                                body = noteDB.body,
                                color = NoteColor.fromArgb(noteDB.color).argb,
                        )
                )
                val response = notesApiProvider.provideApi().saveNote(
                        request = PostNoteRequest(
                                noteId = noteId,
                                folderId = noteDB.folderId,
                                keyId = encryptedPayload.keyId,
                                payload = encryptedPayload.payload,
                                order = noteDB.order,
                                contentUpdatedAt = noteDB.contentUpdatedAt.toServerDateTime(),
                        )
                )
                val noteDTO = response.note
                notesDao.updateUploaded(
                        noteId = noteId,
                        createdAt = noteDTO.createdAt.toEpochMillis(),
                        contentUpdatedAt = noteDTO.contentUpdatedAt.toEpochMillis(),
                )
            }
            SyncStackDB.Action.DELETE -> {
                Napier.d("remove note from server")
                try {
                    notesApiProvider.provideApi().deleteNote(
                            noteId = noteId,
                    )
                } catch (_: Error404Exception) {
                    // a note deleted before its first upload is not on the server: nothing to delete
                    Napier.d("note is not on server")
                }
            }
        }
    }

    private fun mapNoteDBToNoteVO(noteDB: NoteDB): NoteVO {
        return NoteVO(
                id = noteDB.noteId,
                createdAt = noteDB.createdAt,
                contentUpdatedAt = noteDB.contentUpdatedAt,
                title = noteDB.title ?: "",
                body = noteDB.body ?: "",
                color = NoteColor.fromArgb(noteDB.color),
                folderId = noteDB.folderId,
        )
    }

    private fun String.toStoredText(): String? {
        return trim().ifEmpty { null }
    }

    private suspend fun addChangesToSyncStack(
            noteId: String,
            action: SyncStackDB.Action,
    ) {
        val syncStackDB = SyncStackDB(
                itemId = noteId,
                itemType = SyncStackDB.ItemType.NOTE,
                action = action,
        )
        syncStackDao.insertOrReplace(syncStackDB)
    }
}
