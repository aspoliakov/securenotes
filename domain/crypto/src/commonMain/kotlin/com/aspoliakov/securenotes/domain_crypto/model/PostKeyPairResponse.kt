package com.aspoliakov.securenotes.domain_crypto.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class PostKeyPairResponse(
        @SerialName("message") val message: String,
        @SerialName("key") val key: KeyDTO,
)
