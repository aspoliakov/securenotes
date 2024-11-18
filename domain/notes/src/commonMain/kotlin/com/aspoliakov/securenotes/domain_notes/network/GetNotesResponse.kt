package com.aspoliakov.securenotes.domain_notes.network

/**
 * Project SecureNotes
 */

data class GetNotesResponse(
        val notes: List<Note>,
) {
    data class Note(
            val id: String,
            val payload: String,
    )
}
