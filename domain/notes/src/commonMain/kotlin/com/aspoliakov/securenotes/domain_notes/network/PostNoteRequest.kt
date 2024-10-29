package com.aspoliakov.securenotes.domain_notes.network

/**
 * Project SecureNotes
 */

data class PostNoteRequest(
        val noteId: String,
        val title: String?,
        val body: String?,
)
