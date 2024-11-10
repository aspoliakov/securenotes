package com.aspoliakov.securenotes.domain_notes.model

/**
 * Project SecureNotes
 */

data class NotesListItem(
        val id: String,
        val createdAt: Long,
        val title: String?,
        val body: String?,
)
