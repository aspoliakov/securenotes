package com.aspoliakov.securenotes.domain_crypto.sodium

import com.ionspin.kotlin.crypto.generichash.GenericHash
import com.ionspin.kotlin.crypto.util.encodeToUByteArray

/**
 * Project SecureNotes
 */

@OptIn(ExperimentalUnsignedTypes::class)
fun String.hashed(): UByteArray {
    val stringBytes = this.encodeToUByteArray()
    return GenericHash.genericHash(stringBytes)
}
