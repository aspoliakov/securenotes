package com.aspoliakov.securenotes.domain_notes.network

import com.aspoliakov.securenotes.core_network.auth.Authorized
import com.aspoliakov.securenotes.domain_notes.model.DeleteNoteResponse
import com.aspoliakov.securenotes.domain_notes.model.GetNotesResponse
import com.aspoliakov.securenotes.domain_notes.model.PostNoteRequest
import com.aspoliakov.securenotes.domain_notes.model.PostNoteResponse
import de.jensklingenberg.ktorfit.http.*

/**
 * Project SecureNotes
 */

interface NotesApi {

    @Authorized
    @Headers("Content-Type: application/json")
    @POST("save")
    suspend fun saveNote(
            @Body request: PostNoteRequest,
    ): PostNoteResponse

    @Authorized
    @Headers("Content-Type: application/json")
    @GET("all")
    suspend fun getAllNotes(): GetNotesResponse

    @Authorized
    @Headers("Content-Type: application/json")
    @DELETE("{note_id}")
    suspend fun deleteNote(
            @Path("note_id") noteId: String,
    ): DeleteNoteResponse
}
