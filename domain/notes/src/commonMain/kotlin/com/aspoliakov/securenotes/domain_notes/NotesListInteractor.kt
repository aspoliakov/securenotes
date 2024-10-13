package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.model.NoteDB
import com.aspoliakov.securenotes.domain_notes.data.NotesListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Project SecureNotes
 */

class NotesListInteractor(
        private val notesDao: NotesDao,
) {

    fun getNotesList(): Flow<List<NotesListItem>> {
        return notesDao.selectAllByCreatedAtDesc()
                .map { notesList -> notesList.map(this::mapNoteDBToNotesListItem) }
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
}
