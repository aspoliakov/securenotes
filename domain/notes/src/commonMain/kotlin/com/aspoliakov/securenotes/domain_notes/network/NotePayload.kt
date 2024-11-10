package com.aspoliakov.securenotes.domain_notes.network

import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class NotePayload(
        val title: String?,
        val body: String?,
)
