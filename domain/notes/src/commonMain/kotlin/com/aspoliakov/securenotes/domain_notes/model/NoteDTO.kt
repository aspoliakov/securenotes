package com.aspoliakov.securenotes.domain_notes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class NoteDTO(
        @SerialName("note_id") val noteId: String,
        @SerialName("created_at") val createdAt: String,
        @SerialName("updated_at") val updatedAt: String?,
        @SerialName("payload") val payload: String,
)
