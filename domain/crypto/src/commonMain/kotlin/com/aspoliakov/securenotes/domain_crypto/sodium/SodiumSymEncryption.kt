package com.aspoliakov.securenotes.domain_crypto.sodium

import com.ionspin.kotlin.crypto.secretbox.SecretBox
import com.ionspin.kotlin.crypto.util.LibsodiumRandom

/**
 * Project SecureNotes
 */

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.encryptSym(secretKey: UByteArray): SodiumCipher {
    val nonce = LibsodiumRandom.buf(24)
    val data = SecretBox.easy(this, nonce, secretKey)
    return SodiumCipher(nonce, data)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun SodiumCipher.decryptSym(secretKey: UByteArray): UByteArray {
    return SecretBox.openEasy(data, nonce, secretKey)
}
