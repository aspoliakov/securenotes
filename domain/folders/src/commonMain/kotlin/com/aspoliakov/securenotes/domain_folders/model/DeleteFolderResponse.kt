package com.aspoliakov.securenotes.domain_folders.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class DeleteFolderResponse(
        @SerialName("message") val message: String,
)
