package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_base.util.randomUUIDString
import com.aspoliakov.securenotes.core_db.dao.FolderDao
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.event_bus.SyncStackEventBus
import com.aspoliakov.securenotes.core_db.model.NoteDB
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.domain_notes.model.NoteColor
import com.aspoliakov.securenotes.domain_notes.model.NotePayload
import com.aspoliakov.securenotes.domain_notes.model.NoteVO
import com.aspoliakov.securenotes.domain_notes.model.PostNoteRequest
import com.aspoliakov.securenotes.domain_notes.network.NotesApiProvider
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import io.github.aakira.napier.Napier
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Project SecureNotes
 */

class NoteInteractor(
        private val notesDao: NotesDao,
        private val folderDao: FolderDao,
        private val syncStackDao: SyncStackDao,
        private val syncStackEventBus: SyncStackEventBus,
        private val notesApiProvider: NotesApiProvider,
        private val userStateInteractor: UserStateInteractor,
        private val noteCryptoInteractor: NoteCryptoInteractor,
) {

    companion object {
        private const val HANDLE_CHANGES_DELAY = 2000L
        private const val ORDER_STEP = 1000.0
    }

    private val syncJobs: MutableMap<String, Job> = mutableMapOf()

    @OptIn(ExperimentalTime::class)
    suspend fun createNew(folderId: String? = null): String {
        val maxOrder = maxOf(
                notesDao.selectMaxOrderByFolderId(folderId) ?: 0.0,
                folderDao.selectMaxOrderByParentId(folderId) ?: 0.0,
        )
        val newNoteDB = NoteDB(
                noteId = randomUUIDString(),
                createdAt = Clock.System.now().toEpochMilliseconds(),
                folderId = folderId,
                order = maxOrder + ORDER_STEP,
        )
        notesDao.insertOrReplace(newNoteDB)
        return newNoteDB.noteId
    }

    suspend fun reorder(noteId: String, order: Double) {
        notesDao.updateOrder(noteId, order)
        scheduleSync(noteId)
    }

    suspend fun getById(noteId: String): NoteVO? {
        return notesDao.selectById(noteId)?.let {
            NoteVO(
                    id = noteId,
                    createdAt = it.createdAt,
                    title = it.title ?: "",
                    body = it.body ?: "",
                    color = NoteColor.fromArgb(it.color),
                    folderId = it.folderId,
            )
        }
    }

    fun undoCreation(noteId: String) = IOScope().launch {
        val noteDB = notesDao.selectById(noteId)
        val notePayload = createNotePayload(noteDB?.title, noteDB?.body, noteDB?.color)
        if (notePayload is NotePayload.Empty) {
            delete(
                    noteId = noteId,
                    sync = false,
            )
            syncStackDao.delete(noteId)
        }
    }

    suspend fun saveChanges(
            noteId: String,
            title: String,
            body: String,
            color: NoteColor,
    ) {
        val notePayload = createNotePayload(
                title = title,
                body = body,
                color = color.argb,
        )
        when (notePayload) {
            is NotePayload.Empty -> return
            is NotePayload.Payload -> {
                notesDao.updateNote(
                        noteId = noteId,
                        title = notePayload.title,
                        body = notePayload.body,
                        color = notePayload.color,
                )
                scheduleSync(noteId)
            }
        }
    }

    suspend fun delete(
            noteId: String,
            sync: Boolean = true,
    ) {
        syncJobs[noteId]?.cancel()
        notesDao.delete(noteId)
        if (sync) {
            addChangesToSyncStack(noteId, SyncStackDB.Action.DELETE)
        }
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
                notesApiProvider.provideApi().saveNote(
                        token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
                        request = PostNoteRequest(
                                noteId = noteId,
                                folderId = noteDB.folderId,
                                keyId = encryptedPayload.keyId,
                                payload = encryptedPayload.payload,
                                order = noteDB.order,
                        )
                )
            }
            SyncStackDB.Action.DELETE -> {
                Napier.d("remove note from server")
                notesApiProvider.provideApi().deleteNote(
                        token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
                        noteId = noteId,
                )
            }
        }
    }

    private fun createNotePayload(
            title: String?,
            body: String?,
            color: Long?,
    ): NotePayload {
        val processedTitleValue = title?.trim().takeIf { !it.isNullOrBlank() }
        val processedBodyValue = body?.trim().takeIf { !it.isNullOrBlank() }
        return if (processedTitleValue == null && processedBodyValue == null) {
            NotePayload.Empty
        } else {
            NotePayload.Payload(
                    title = processedTitleValue,
                    body = processedBodyValue,
                    color = NoteColor.fromArgb(color).argb,
            )
        }
    }

    private fun scheduleSync(noteId: String) {
        syncJobs[noteId]?.cancel()
        syncJobs[noteId] = IOScope().launch {
            delay(HANDLE_CHANGES_DELAY.milliseconds)
            addChangesToSyncStack(noteId, SyncStackDB.Action.SAVE)
        }
    }

    private fun addChangesToSyncStack(
            noteId: String,
            action: SyncStackDB.Action,
    ) = IOScope().launch {
        val syncStackDB = SyncStackDB(
                itemId = noteId,
                itemType = SyncStackDB.ItemType.NOTE,
                action = action,
        )
        syncStackDao.insertOrReplace(syncStackDB)
        syncStackEventBus.post(noteId)
    }

    sealed class NotePayload {
        data object Empty : NotePayload()
        data class Payload(
                val title: String?,
                val body: String?,
                val color: Long?,
        ) : NotePayload()
    }
}
