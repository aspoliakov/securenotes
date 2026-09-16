package com.aspoliakov.securenotes.domain_folders.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class FolderDTO(
        @SerialName("folder_id") val folderId: String,
        @SerialName("parent_id") val parentId: String?,
        @SerialName("created_at") val createdAt: String,
        @SerialName("updated_at") val updatedAt: String?,
        @SerialName("payload") val payload: String,
)
