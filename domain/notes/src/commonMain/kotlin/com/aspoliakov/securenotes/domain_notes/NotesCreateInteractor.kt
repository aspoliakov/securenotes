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

    suspend fun addMockNote() {
        val id = randomUUIDString()
        val noteDB = NoteDB(
                id = id,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                title = if (id.hashCode() % 2 == 0) {
                    "Title ${id.take(3)}"
                } else {
                    null
                },
                body = "note body ${id.take(3)}",
        )
        notesDao.insertOrReplace(noteDB)
    }
}
