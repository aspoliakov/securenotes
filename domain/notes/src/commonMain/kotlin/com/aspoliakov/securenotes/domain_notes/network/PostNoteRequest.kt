package com.aspoliakov.securenotes.domain_notes.network

/**
 * Project SecureNotes
 */

data class PostNoteRequest(
        val noteId: String,
        val keyId: String,
        val payload: String,
)
