package com.aspoliakov.securenotes.domain_crypto

import com.aspoliakov.securenotes.core_key_value_storage.EncryptedKeyValueStorage
import com.aspoliakov.securenotes.domain_crypto.model.KeyDTO
import com.aspoliakov.securenotes.domain_crypto.model.PostKeyPairRequest
import com.aspoliakov.securenotes.domain_crypto.network.KeysApiProvider
import com.aspoliakov.securenotes.domain_crypto.sodium.*
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor

/**
 * Project SecureNotes
 */

@OptIn(ExperimentalUnsignedTypes::class)
class UserKeysInteractor(
        private val userStateInteractor: UserStateInteractor,
        private val encryptedKeyValueStorage: EncryptedKeyValueStorage,
        private val keysApiProvider: KeysApiProvider,
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
        return runCatching {
            val response = keysApiProvider.provideApi().postNewUserKey(
                    token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
                    request = PostKeyPairRequest(
                            publicKey = encodedPublicKey,
                            encryptedPrivateKey = encryptedPrivateKey,
                    ),
            )
            encryptedKeyValueStorage.put(USER_KEY_PAIR_ID, response.key.keyId)
            encryptedKeyValueStorage.put(USER_PUBLIC_KEY, publicKey.encodeBase64())
            encryptedKeyValueStorage.put(USER_PRIVATE_KEY, privateKey.encodeBase64())
            userStateInteractor.setUserActive()
            KeysCreateResult.OK
        }.getOrElse {
            KeysCreateResult.NETWORK_ERROR
        }
    }

    suspend fun loadKey(): KeyDTO? {
        val allUserKeys = keysApiProvider.provideApi().getAllUserKeys(
                token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
        ).keys
        return allUserKeys.takeIf { it.isNotEmpty() }?.maxBy { it.version }
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
