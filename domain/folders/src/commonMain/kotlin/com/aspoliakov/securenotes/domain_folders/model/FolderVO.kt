package com.aspoliakov.securenotes.domain_folders.model

/**
 * Project SecureNotes
 */

data class FolderVO(
        val id: String,
        val name: String,
        val parentId: String?,
        val createdAt: Long,
)
