package com.aspoliakov.securenotes.domain_folders.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class PostFolderRequest(
        @SerialName("folder_id") val folderId: String,
        @SerialName("parent_id") val parentId: String?,
        @SerialName("key_id") val keyId: String,
        @SerialName("payload") val payload: String,
        @SerialName("order") val order: Double,
)
