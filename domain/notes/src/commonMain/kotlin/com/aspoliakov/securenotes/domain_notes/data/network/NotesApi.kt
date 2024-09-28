package com.aspoliakov.securenotes.domain_notes.data.network

/**
 * Project SecureNotes
 */

interface NotesApi {
    suspend fun getNotes(from: Long, size: Int): List<NotesResponse>
}
