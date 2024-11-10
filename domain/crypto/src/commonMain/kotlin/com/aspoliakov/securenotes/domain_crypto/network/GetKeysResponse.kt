package com.aspoliakov.securenotes.domain_crypto.network

/**
 * Project SecureNotes
 */
data class GetKeysResponse(
        val keys: List<KeyPair>
) {
    data class KeyPair(
            val id: String,
            val publicKey: String,
            val encryptedPrivateKey: String,
            val version: Int,
    )
}
