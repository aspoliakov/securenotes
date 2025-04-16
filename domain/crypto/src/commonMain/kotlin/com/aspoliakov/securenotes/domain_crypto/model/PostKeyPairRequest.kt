package com.aspoliakov.securenotes.domain_crypto.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class PostKeyPairRequest(
        @SerialName("public_key") val publicKey: String,
        @SerialName("encrypted_private_key") val encryptedPrivateKey: String,
)
