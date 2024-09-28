package com.aspoliakov.securenotes.domain_notes.data.folders

/**
 * Project SecureNotes
 */

sealed class NotesListItem(
    open val id: String,
    open val createdAt: Long,
) {

    data class Note(
            override val id: String,
            override val createdAt: Long,
            val body: String,
    ) : NotesListItem(id, createdAt)

    data class Folder(
        override val id: String,
        override val createdAt: Long,
        val name: String,
    ) : NotesListItem(id, createdAt)
}
