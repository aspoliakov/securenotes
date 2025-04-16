package com.aspoliakov.securenotes.domain_notes.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class NotePayload(
        @SerialName("title") val title: String?,
        @SerialName("body") val body: String?,
)
