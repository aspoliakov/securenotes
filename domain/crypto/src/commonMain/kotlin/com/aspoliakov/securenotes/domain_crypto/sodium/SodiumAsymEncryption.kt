package com.aspoliakov.securenotes.domain_crypto.sodium

import com.ionspin.kotlin.crypto.box.Box
import com.ionspin.kotlin.crypto.util.LibsodiumRandom

/**
 * Project SecureNotes
 */

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.encryptAsym(publicKey: UByteArray, privateKey: UByteArray): SodiumCipher {
    val nonce = LibsodiumRandom.buf(24)
    val data = Box.easy(this, nonce, publicKey, privateKey)
    return SodiumCipher(nonce, data)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun SodiumCipher.decryptAsym(publicKey: UByteArray, privateKey: UByteArray): UByteArray {
    return Box.openEasy(data, nonce, publicKey, privateKey)
}
