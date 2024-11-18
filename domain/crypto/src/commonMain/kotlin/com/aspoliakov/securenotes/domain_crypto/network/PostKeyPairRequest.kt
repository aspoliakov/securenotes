package com.aspoliakov.securenotes.domain_crypto.network

/**
 * Project SecureNotes
 */
data class PostKeyPairRequest(
        val id: String,
        val publicKey: String,
        val encryptedPrivateKey: String,
        val version: Int,
)
