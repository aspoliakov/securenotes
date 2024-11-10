@file:OptIn(ExperimentalUnsignedTypes::class)

package com.aspoliakov.securenotes.domain_crypto.sodium

/**
 * Project SecureNotes
 */

data class SodiumCipher(
        val nonce: UByteArray,
        val data: UByteArray,
)

fun String.cipherFromBase64(): SodiumCipher {
    val cipherBase64Bytes = this.decodeBase64()
    val nonce = cipherBase64Bytes.take(24).toUByteArray()
    val data = cipherBase64Bytes.slice(24..cipherBase64Bytes.lastIndex).toUByteArray()
    return SodiumCipher(nonce, data)
}

fun SodiumCipher.toBase64(): String {
    return (nonce + data).encodeBase64()
}
