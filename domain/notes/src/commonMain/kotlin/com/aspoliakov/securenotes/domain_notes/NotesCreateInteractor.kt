package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_base.util.randomUUIDString
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.model.NoteDB
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Project SecureNotes
 */

class NotesCreateInteractor(
        private val notesDao: NotesDao,
) {

    fun addNote(
            title: String,
            body: String,
    ) = IOScope().launch {
        val processedTitleValue = title.trim().takeIf { it.isNotBlank() }
        val processedBodyValue = body.trim().takeIf { it.isNotBlank() }
        if (processedTitleValue == null && processedBodyValue == null) return@launch
        val newNoteDB = NoteDB(
                id = randomUUIDString(),
                createdAt = Clock.System.now().toEpochMilliseconds(),
                title = processedTitleValue,
                body = processedBodyValue,
        )
        notesDao.insertOrReplace(newNoteDB)
    }
}
