package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_base.util.randomUUIDString
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.event_bus.SyncStackEventBus
import com.aspoliakov.securenotes.core_db.model.NoteDB
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.domain_notes.model.NotePayload
import com.aspoliakov.securenotes.domain_notes.model.NoteVO
import com.aspoliakov.securenotes.domain_notes.model.PostNoteRequest
import com.aspoliakov.securenotes.domain_notes.network.NotesApiProvider
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Project SecureNotes
 */

class NoteInteractor(
        private val notesDao: NotesDao,
        private val syncStackDao: SyncStackDao,
        private val syncStackEventBus: SyncStackEventBus,
        private val notesApiProvider: NotesApiProvider,
        private val userStateInteractor: UserStateInteractor,
        private val noteCryptoInteractor: NoteCryptoInteractor,
) {

    companion object {
        private const val HANDLE_CHANGES_DELAY = 2000L
    }

    private val saveChangesJobs: MutableMap<String, Job> = mutableMapOf()

    suspend fun createNew(): String {
        val newNoteDB = NoteDB(
                noteId = randomUUIDString(),
                createdAt = Clock.System.now().toEpochMilliseconds(),
        )
        notesDao.insertOrReplace(newNoteDB)
        return newNoteDB.noteId
    }

    suspend fun getById(noteId: String): NoteVO? {
        return notesDao.selectById(noteId)?.let {
            NoteVO(
                    createdAt = it.createdAt,
                    title = it.title ?: "",
                    body = it.body ?: "",
            )
        }
    }

    fun undoCreation(noteId: String) = IOScope().launch {
        delay(HANDLE_CHANGES_DELAY)
        val noteDB = notesDao.selectById(noteId)
        val notePayload = createNotePayload(noteDB?.title, noteDB?.body)
        if (notePayload is NotePayload.Empty) {
            delete(
                    noteId = noteId,
                    sync = false,
            )
            syncStackDao.delete(noteId)
        }
    }

    fun saveChanges(
            noteId: String,
            title: String,
            body: String,
    ) {
        val currentJob = saveChangesJobs[noteId]
        currentJob?.cancel()
        saveChangesJobs[noteId] = IOScope().launch {
            delay(HANDLE_CHANGES_DELAY)
            val notePayload = createNotePayload(
                    title = title,
                    body = body,
            )
            when (notePayload) {
                is NotePayload.Empty -> return@launch
                is NotePayload.Payload -> {
                    notesDao.updateNote(
                            noteId = noteId,
                            title = notePayload.title,
                            body = notePayload.body,
                    )
                    addChangesToSyncStack(noteId)
                }
            }
        }
    }

    suspend fun syncChanges(noteId: String) {
        val noteDB = notesDao.selectById(noteId)
        if (noteDB != null) {
            Napier.d("upload updated note to server")
            val encryptedPayload = noteCryptoInteractor.encrypt(
                    NotePayload(
                            title = noteDB.title,
                            body = noteDB.body,
                    )
            )
            notesApiProvider.provideApi().saveNote(
                    token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
                    request = PostNoteRequest(
                            noteId = noteId,
                            keyId = encryptedPayload.keyId,
                            payload = encryptedPayload.payload,
                    )
            )
        } else {
            Napier.d("remove note from server")
            notesApiProvider.provideApi().deleteNote(
                    token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
                    noteId = noteId,
            )
        }
    }

    suspend fun delete(
            noteId: String,
            sync: Boolean = true,
    ) {
        saveChangesJobs[noteId]?.cancel()
        notesDao.delete(noteId)
        if (sync) {
            addChangesToSyncStack(noteId)
        }
    }

    private fun createNotePayload(
            title: String?,
            body: String?,
    ): NotePayload {
        val processedTitleValue = title?.trim().takeIf { !it.isNullOrBlank() }
        val processedBodyValue = body?.trim().takeIf { !it.isNullOrBlank() }
        return if (processedTitleValue == null && processedBodyValue == null) {
            NotePayload.Empty
        } else {
            NotePayload.Payload(
                    title = processedTitleValue,
                    body = processedBodyValue,
            )
        }
    }

    private fun addChangesToSyncStack(noteId: String) = IOScope().launch {
        val syncStackDB = SyncStackDB(
                itemId = noteId,
                itemType = SyncStackDB.ItemType.NOTE,
        )
        syncStackDao.insertOrReplace(syncStackDB)
        syncStackEventBus.post(noteId)
    }

    sealed class NotePayload {
        data object Empty : NotePayload()
        data class Payload(
                val title: String?,
                val body: String?,
        ) : NotePayload()
    }
}
