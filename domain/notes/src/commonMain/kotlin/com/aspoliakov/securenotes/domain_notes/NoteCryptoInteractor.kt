package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.domain_crypto.UserKeysProvider
import com.aspoliakov.securenotes.domain_crypto.decryptText
import com.aspoliakov.securenotes.domain_crypto.encryptText
import com.aspoliakov.securenotes.domain_notes.network.NotePayload
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Project SecureNotes
 */

class NoteCryptoInteractor(
        private val userKeysProvider: UserKeysProvider,
) {

    fun encrypt(
            payload: NotePayload,
    ): EncryptedNote {
        val userKeyPair = userKeysProvider.getUserKeyPair()
        return EncryptedNote(
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
    ): NotePayload {
        val userKeyPair = userKeysProvider.getUserKeyPair()
        return Json.decodeFromString<NotePayload>(
                payload.decryptText(
                        publicKey = userKeyPair.publicKey,
                        privateKey = userKeyPair.privateKey,
                )
        )
    }
}

data class EncryptedNote(
        val keyId: String,
        val payload: String,
)
