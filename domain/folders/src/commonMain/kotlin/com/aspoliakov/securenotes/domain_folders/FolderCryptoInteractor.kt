package com.aspoliakov.securenotes.domain_folders

import com.aspoliakov.securenotes.domain_crypto.UserKeysProvider
import com.aspoliakov.securenotes.domain_crypto.decryptText
import com.aspoliakov.securenotes.domain_crypto.encryptText
import com.aspoliakov.securenotes.domain_folders.model.FolderPayload
import kotlinx.serialization.json.Json

/**
 * Project SecureNotes
 */

class FolderCryptoInteractor(
        private val userKeysProvider: UserKeysProvider,
) {

    fun encrypt(
            payload: FolderPayload,
    ): EncryptedFolder {
        val userKeyPair = userKeysProvider.getUserKeyPair()
        return EncryptedFolder(
                keyId = userKeyPair.keyId,
                payload = Json
                        .encodeToString(payload)
                        .encryptText(
                                publicKey = userKeyPair.publicKey,
                                privateKey = userKeyPair.privateKey,
                        ),
        )
    }

    fun decrypt(
            payload: String,
    ): FolderPayload {
        val userKeyPair = userKeysProvider.getUserKeyPair()
        return Json.decodeFromString<FolderPayload>(
                payload.decryptText(
                        publicKey = userKeyPair.publicKey,
                        privateKey = userKeyPair.privateKey,
                )
        )
    }
}

data class EncryptedFolder(
        val keyId: String,
        val payload: String,
)
