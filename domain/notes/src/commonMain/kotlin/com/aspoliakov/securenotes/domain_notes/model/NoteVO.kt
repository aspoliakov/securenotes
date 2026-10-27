package com.aspoliakov.securenotes.domain_notes.model

/**
 * Project SecureNotes
 */
data class NoteVO(
        val id: String,
        var createdAt: Long,
        var contentUpdatedAt: Long,
        var title: String = "",
        var body: String = "",
        var color: NoteColor = NoteColor.DEFAULT,
        var hidden: Boolean = false,
        var folderId: String? = null,
        var order: Double = 0.0,
)
