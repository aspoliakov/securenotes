package com.aspoliakov.securenotes.domain_notes.network

/**
 * Project SecureNotes
 */

interface NotesApi {

    suspend fun getAllNotes(): GetNotesResponse

    suspend fun postNote(request: PostNoteRequest)

    suspend fun deleteNote(noteId: String)
}
