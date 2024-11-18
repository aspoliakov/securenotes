package com.aspoliakov.securenotes.domain_crypto

import com.aspoliakov.securenotes.core_base.util.randomUUIDString
import com.aspoliakov.securenotes.core_key_value_storage.EncryptedKeyValueStorage
import com.aspoliakov.securenotes.domain_crypto.network.GetKeysResponse
import com.aspoliakov.securenotes.domain_crypto.network.KeysApi
import com.aspoliakov.securenotes.domain_crypto.network.PostKeyPairRequest
import com.aspoliakov.securenotes.domain_crypto.sodium.SodiumKeyPair
import com.aspoliakov.securenotes.domain_crypto.sodium.cipherFromBase64
import com.aspoliakov.securenotes.domain_crypto.sodium.decryptSym
import com.aspoliakov.securenotes.domain_crypto.sodium.encodeBase64
import com.aspoliakov.securenotes.domain_crypto.sodium.encryptSym
import com.aspoliakov.securenotes.domain_crypto.sodium.hashed
import com.aspoliakov.securenotes.domain_crypto.sodium.toBase64
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor

/**
 * Project SecureNotes
 */

@OptIn(ExperimentalUnsignedTypes::class)
class UserKeysInteractor(
        private val userStateInteractor: UserStateInteractor,
        private val encryptedKeyValueStorage: EncryptedKeyValueStorage,
        private val keysApi: KeysApi,
) {

    companion object {
        internal const val USER_KEY_PAIR_ID = "user_key_pair_id"
        internal const val USER_PUBLIC_KEY = "user_public_key"
        internal const val USER_PRIVATE_KEY = "user_private_key"
    }

    suspend fun createKey(userPassword: String): KeysCreateResult {
        val hashedPassword = userPassword.hashed()
        val keyPair = SodiumKeyPair()
        val publicKey = keyPair.getPublicKey()
        val privateKey = keyPair.getPrivateKey()
        val encodedPublicKey = publicKey
                .encodeBase64()
        val encryptedPrivateKey = privateKey
                .encryptSym(hashedPassword)
                .toBase64()
        val postKeyPairRequest = PostKeyPairRequest(
                id = randomUUIDString(),
                publicKey = encodedPublicKey,
                encryptedPrivateKey = encryptedPrivateKey,
                version = 1,
        )
        return runCatching {
            deleteAllUserKeys()
            keysApi.postNewUserKey(postKeyPairRequest)
            encryptedKeyValueStorage.put(USER_KEY_PAIR_ID, postKeyPairRequest.id)
            encryptedKeyValueStorage.put(USER_PUBLIC_KEY, publicKey.encodeBase64())
            encryptedKeyValueStorage.put(USER_PRIVATE_KEY, privateKey.encodeBase64())
            userStateInteractor.setUserActive()
            KeysCreateResult.OK
        }.getOrElse {
            KeysCreateResult.NETWORK_ERROR
        }
    }

    suspend fun loadKey(): GetKeysResponse.KeyPair? {
        return keysApi.getUserKeys().keys.takeIf { it.isNotEmpty() }?.maxBy { it.version }
    }

    suspend fun restoreKey(
            password: String,
            keyId: String,
            publicKey: String,
            encryptedPrivateKey: String,
    ): KeysRestoreResult {
        return runCatching {
            val cipher = encryptedPrivateKey.cipherFromBase64()
            val hashedPassword = password.hashed()
            val decryptedPrivateKey = cipher.decryptSym(hashedPassword)
            encryptedKeyValueStorage.put(USER_KEY_PAIR_ID, keyId)
            encryptedKeyValueStorage.put(USER_PUBLIC_KEY, publicKey)
            encryptedKeyValueStorage.put(USER_PRIVATE_KEY, decryptedPrivateKey.encodeBase64())
            userStateInteractor.setUserActive()
            KeysRestoreResult.OK
        }.getOrElse {
            KeysRestoreResult.FAILED
        }
    }

    private suspend fun deleteAllUserKeys() {
        val currentKeys = keysApi.getUserKeys().keys
        currentKeys.forEach { keysApi.deleteUserKey(it.id) }
    }
}

enum class KeysCreateResult {
    OK,
    NETWORK_ERROR,
    UNEXPECTED_ERROR,
}

enum class KeysRestoreResult {
    OK,
    FAILED,
}
