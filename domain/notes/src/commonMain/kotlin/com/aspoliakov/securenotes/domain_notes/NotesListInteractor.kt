package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.dao.NotesFoldersDao
import com.aspoliakov.securenotes.core_db.model.NoteDB
import com.aspoliakov.securenotes.core_db.model.NoteFolderDB
import com.aspoliakov.securenotes.domain_notes.data.folders.NotesListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Project SecureNotes
 */

class NotesListInteractor(
        private val notesFoldersDao: NotesFoldersDao,
        private val notesDao: NotesDao,
) {

    fun getNotesListTest(): Flow<List<NotesListItem>> {
        return notesDao.selectAll()
                .map { notesDBList -> notesDBList.map(this::mapNoteDBToNotesListItem) }
    }

    fun getNotesList(parentId: String? = null): Flow<List<NotesListItem>> {
        return notesFoldersDao.selectAllByCreatedAtDesc(parentId
                ?: NoteFolderDB.ROOT_FOLDER, Int.MAX_VALUE)
                .combine(notesDao.selectAllByCreatedAtDesc(parentId
                        ?: NoteFolderDB.ROOT_FOLDER, Int.MAX_VALUE))
                { folders, notes ->
                    (folders.map(this::mapFolderDBToNotesListItem) + notes.map(this::mapNoteDBToNotesListItem))
                            .sortedBy { it.createdAt }
                }
    }

    private fun mapFolderDBToNotesListItem(noteFolderDB: NoteFolderDB): NotesListItem {
        return NotesListItem.Folder(noteFolderDB.id, noteFolderDB.createdAt, noteFolderDB.name)
    }

    private fun mapNoteDBToNotesListItem(noteDB: NoteDB): NotesListItem {
        return NotesListItem.Note(noteDB.id, noteDB.createdAt, noteDB.body)
    }
}
