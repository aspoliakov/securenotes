package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_base.util.randomUUIDString
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.event_bus.SyncStackEventBus
import com.aspoliakov.securenotes.core_db.model.NoteDB
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
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
) {

    private val saveChangesJobs: MutableMap<String, Job> = mutableMapOf()

    suspend fun createNew(): String {
        val newNoteDB = NoteDB(
                id = randomUUIDString(),
                createdAt = Clock.System.now().toEpochMilliseconds(),
        )
        notesDao.insertOrReplace(newNoteDB)
        return newNoteDB.id
    }

    fun undoCreation(noteId: String) = IOScope().launch {
        Napier.d("Undo creation")
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
            delay(2000)
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
                    syncViaStack(noteId)
                }
            }
        }
    }

    suspend fun syncChanges(noteId: String) {
        val noteDB = notesDao.selectById(noteId)
        if (noteDB != null) {
            Napier.e("upload updated note to server")
            // TODO upload updated note to server
        } else {
            Napier.e("remove note from server")
            // TODO remove note from server
        }
    }

    suspend fun delete(
            noteId: String,
            sync: Boolean = true,
    ) {
        saveChangesJobs[noteId]?.cancel()
        notesDao.delete(noteId)
        if (sync) {
            syncViaStack(noteId)
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

    private fun syncViaStack(noteId: String) = IOScope().launch {
        val syncStackDB = SyncStackDB(
                id = noteId,
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
