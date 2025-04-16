package com.aspoliakov.securenotes.core_network.exceptions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class ErrorResponse(
        @SerialName("detail") val detail: String?,
)
