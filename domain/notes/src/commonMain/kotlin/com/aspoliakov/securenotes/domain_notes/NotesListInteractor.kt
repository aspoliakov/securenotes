package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.model.NoteDB
import com.aspoliakov.securenotes.domain_notes.model.NotesListItem
import com.aspoliakov.securenotes.domain_notes.network.NotesApi
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Project SecureNotes
 */

class NotesListInteractor(
        private val notesDao: NotesDao,
        private val notesApi: NotesApi,
        private val noteCryptoInteractor: NoteCryptoInteractor,
) {

    fun getNotesList(): Flow<List<NotesListItem>> {
        return notesDao.selectAllByCreatedAtDesc()
                .map { notesList -> notesList.map(this::mapNoteDBToNotesListItem) }
                .also { sync() }
    }

    suspend fun searchNotesList(query: String): List<NotesListItem> {
        return notesDao.searchAllByCreatedAtDesc()
                .map(this::mapNoteDBToNotesListItem)
    }

    private fun mapNoteDBToNotesListItem(noteDB: NoteDB): NotesListItem {
        return NotesListItem(
                id = noteDB.id,
                createdAt = noteDB.createdAt,
                title = noteDB.title,
                body = noteDB.body,
        )
    }

    private fun sync() = IOScope().launch {
        runCatching {
            notesApi.getAllNotes()
                    .notes
                    .map {
                        val notePayload = noteCryptoInteractor.decrypt(it.payload)
                        val noteDBList = NoteDB(
                                id = it.id,
                                createdAt = 1, // TODO
                                title = notePayload.title,
                                body = notePayload.body,
                        )
                        notesDao.insertOrReplace(noteDBList)
                    }
        }
                .onFailure {
                    Napier.e("Error syncing notes: $it")
                }
    }
}
