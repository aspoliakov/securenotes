package com.aspoliakov.securenotes.domain_notes.network

/**
 * Project SecureNotes
 */

data class GetNotesResponse(
        val id: String,
        val title: String?,
        val body: String?,
)
