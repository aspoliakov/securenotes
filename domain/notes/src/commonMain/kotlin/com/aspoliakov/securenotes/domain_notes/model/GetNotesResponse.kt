package com.aspoliakov.securenotes.domain_notes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class GetNotesResponse(
        @SerialName("message") val message: String?,
        @SerialName("notes") val notes: List<NoteDTO>,
)
