package com.aspoliakov.securenotes.domain_crypto.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class GetKeysResponse(
        @SerialName("message") val message: String?,
        @SerialName("keys") val keys: List<KeyDTO>,
)
