package com.aspoliakov.securenotes.domain_crypto

import com.aspoliakov.securenotes.domain_crypto.sodium.cipherFromBase64
import com.aspoliakov.securenotes.domain_crypto.sodium.decodeBase64
import com.aspoliakov.securenotes.domain_crypto.sodium.decryptAsym
import com.aspoliakov.securenotes.domain_crypto.sodium.encryptAsym
import com.aspoliakov.securenotes.domain_crypto.sodium.toBase64
import com.ionspin.kotlin.crypto.util.decodeFromUByteArray
import com.ionspin.kotlin.crypto.util.encodeToUByteArray

/**
 * Project SecureNotes
 */

@OptIn(ExperimentalUnsignedTypes::class)
fun String.encryptText(publicKey: String, privateKey: String): String {
    return this.encodeToUByteArray()
            .encryptAsym(
                    publicKey = publicKey.decodeBase64(),
                    privateKey = privateKey.decodeBase64(),
            )
            .toBase64()
}

@OptIn(ExperimentalUnsignedTypes::class)
fun String.decryptText(publicKey: String, privateKey: String): String {
    return this.cipherFromBase64()
            .decryptAsym(
                    publicKey = publicKey.decodeBase64(),
                    privateKey = privateKey.decodeBase64(),
            )
            .decodeFromUByteArray()
}
