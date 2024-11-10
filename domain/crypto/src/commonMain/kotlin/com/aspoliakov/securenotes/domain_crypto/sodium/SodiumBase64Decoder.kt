package com.aspoliakov.securenotes.domain_crypto.sodium

import com.ionspin.kotlin.crypto.util.LibsodiumUtil

/**
 * Project SecureNotes
 */

@OptIn(ExperimentalUnsignedTypes::class)
fun String.decodeBase64(): UByteArray {
    return LibsodiumUtil.fromBase64(this)
}
