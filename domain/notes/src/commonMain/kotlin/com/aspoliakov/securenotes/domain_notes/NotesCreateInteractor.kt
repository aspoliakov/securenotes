package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_base.util.randomUUIDString
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.model.NoteDB
import kotlinx.datetime.Clock

/**
 * Project SecureNotes
 */

class NotesCreateInteractor(
        private val notesDao: NotesDao,
) {

    suspend fun addNewNotes(folderId: String) {
        val notes = mutableListOf<NoteDB>()
        for (i in 1..5) {
            val id = randomUUIDString()
            notes.add(
                    NoteDB(
                            id = id,
                            folderId = folderId,
                            createdAt = Clock.System.now().toEpochMilliseconds(),
                            body = "note${id.take(3)}",
                    )
            )
        }
        notesDao.insertOrReplace(notes)
    }
}
