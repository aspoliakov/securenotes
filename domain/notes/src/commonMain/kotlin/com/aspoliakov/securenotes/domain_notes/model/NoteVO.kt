package com.aspoliakov.securenotes.domain_notes.model

/**
 * Project SecureNotes
 */
data class NoteVO(
        var createdAt: Long,
        var title: String = "",
        var body: String = "",
)
