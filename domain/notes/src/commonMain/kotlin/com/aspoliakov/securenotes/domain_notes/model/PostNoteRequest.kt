package com.aspoliakov.securenotes.domain_notes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class PostNoteRequest(
        @SerialName("note_id") val noteId: String,
        @SerialName("key_id") val keyId: String,
        @SerialName("payload") val payload: String,
)
