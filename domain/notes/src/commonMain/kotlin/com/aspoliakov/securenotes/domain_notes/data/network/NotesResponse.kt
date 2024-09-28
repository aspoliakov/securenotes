package com.aspoliakov.securenotes.domain_notes.data.network

/**
 * Project SecureNotes
 */

data class NotesResponse(
    val id: String,
    val name: String,
    val createdAt: Long,
    val editedAt: Long?
)
