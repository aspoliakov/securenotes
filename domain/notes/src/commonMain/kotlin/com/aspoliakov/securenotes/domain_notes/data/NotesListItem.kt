package com.aspoliakov.securenotes.domain_notes.data

/**
 * Project SecureNotes
 */

data class NotesListItem(
        val id: String,
        val createdAt: Long,
        val title: String?,
        val body: String?,
)
