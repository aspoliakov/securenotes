package com.aspoliakov.securenotes.domain_crypto.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class KeyDTO(
        @SerialName("key_id") val keyId: String,
        @SerialName("created_at") val createdAt: String,
        @SerialName("updated_at") val updatedAt: String?,
        @SerialName("public_key") val publicKey: String,
        @SerialName("encrypted_private_key") val encryptedPrivateKey: String,
        @SerialName("version") val version: String,
)
