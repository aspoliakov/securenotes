package com.aspoliakov.securenotes.domain_crypto

import com.aspoliakov.securenotes.core_key_value_storage.EncryptedKeyValueStorage
import com.aspoliakov.securenotes.domain_crypto.UserKeysInteractor.Companion.USER_KEY_PAIR_ID
import com.aspoliakov.securenotes.domain_crypto.UserKeysInteractor.Companion.USER_PRIVATE_KEY
import com.aspoliakov.securenotes.domain_crypto.UserKeysInteractor.Companion.USER_PUBLIC_KEY

/**
 * Project SecureNotes
 */

class UserKeysProvider(
        private val encryptedKeyValueStorage: EncryptedKeyValueStorage,
) {

    fun getUserKeyPair(): UserKeyPair {
        val keyId = encryptedKeyValueStorage.getString(USER_KEY_PAIR_ID)
        val publicKey = encryptedKeyValueStorage.getString(USER_PUBLIC_KEY)
        val privateKey = encryptedKeyValueStorage.getString(USER_PRIVATE_KEY)
        return if (keyId != null && publicKey != null && privateKey != null) {
            UserKeyPair(
                    keyId = keyId,
                    publicKey = publicKey,
                    privateKey = privateKey,
            )
        } else {
            throw IllegalStateException("Wrong user encryption key pair")
        }
    }
}

data class UserKeyPair(
        val keyId: String,
        val publicKey: String,
        val privateKey: String,
)
