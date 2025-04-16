package com.aspoliakov.securenotes.domain_notes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class PostNoteResponse(
        @SerialName("message") val message: String,
        @SerialName("note") val key: NoteDTO,
)
