package com.aspoliakov.securenotes.domain_crypto.sodium

import com.ionspin.kotlin.crypto.util.LibsodiumUtil

/**
 * Project SecureNotes
 */

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.encodeBase64(): String {
    return LibsodiumUtil.toBase64(this)
}
