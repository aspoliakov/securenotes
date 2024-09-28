package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_base.util.randomUUIDString
import com.aspoliakov.securenotes.core_db.dao.NotesFoldersDao
import com.aspoliakov.securenotes.core_db.model.NoteFolderDB
import kotlinx.datetime.Clock

/**
 * Project SecureNotes
 */

class FolderCreateInteractor(
        private val notesFoldersDao: NotesFoldersDao,
        private val notesCreateInteractor: NotesCreateInteractor,
) {

    suspend fun addNewFolder() {
        val randomUuid = randomUUIDString()
        notesFoldersDao.insertOrReplace(
                NoteFolderDB(
                        id = randomUuid,
                        name = "Name ${randomUuid.take(5)}",
                        createdAt = Clock.System.now().toEpochMilliseconds(),
                )
        )
    }

    suspend fun addRandomNotesList() {
        val folders = mutableListOf<NoteFolderDB>()
        notesCreateInteractor.addNewNotes(NoteFolderDB.ROOT_FOLDER)
        for (i in 1..5) {
            val parentUuid = randomUUIDString()
            notesCreateInteractor.addNewNotes(parentUuid)
            folders.add(
                    NoteFolderDB(
                            id = parentUuid,
                            name = "Name: $i",
                            createdAt = Clock.System.now().toEpochMilliseconds(),
                    )
            )
            for (j in 1..5) {
                val childUuid = randomUUIDString()
                notesCreateInteractor.addNewNotes(childUuid)
                folders.add(
                        NoteFolderDB(
                                id = childUuid,
                                parent_id = parentUuid,
                                name = "Name: $i-$j",
                                createdAt = Clock.System.now().toEpochMilliseconds(),
                        )
                )
            }
        }
        notesFoldersDao.insertOrReplace(folders)
    }
}
