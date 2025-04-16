package com.aspoliakov.securenotes.domain_notes.network

import com.aspoliakov.securenotes.domain_notes.model.DeleteNoteResponse
import com.aspoliakov.securenotes.domain_notes.model.GetNotesResponse
import com.aspoliakov.securenotes.domain_notes.model.PostNoteRequest
import com.aspoliakov.securenotes.domain_notes.model.PostNoteResponse
import de.jensklingenberg.ktorfit.http.*

/**
 * Project SecureNotes
 */

interface NotesApi {

    @Headers("Content-Type: application/json")
    @POST("save")
    suspend fun saveNote(
            @Header("access_token") token: String,
            @Body request: PostNoteRequest,
    ): PostNoteResponse

    @Headers("Content-Type: application/json")
    @GET("all")
    suspend fun getAllNotes(
            @Header("access_token") token: String,
    ): GetNotesResponse

    @Headers("Content-Type: application/json")
    @DELETE("{note_id}")
    suspend fun deleteNote(
            @Header("access_token") token: String,
            @Path("note_id") noteId: String,
    ): DeleteNoteResponse
}
