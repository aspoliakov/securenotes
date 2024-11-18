package com.aspoliakov.securenotes.domain_crypto.sodium

import com.ionspin.kotlin.crypto.box.Box
import com.ionspin.kotlin.crypto.util.LibsodiumRandom

/**
 * Project SecureNotes
 */

@OptIn(ExperimentalUnsignedTypes::class)
class SodiumKeyPair {

    private val keyPair = Box.seedKeypair(LibsodiumRandom.buf(32))

    fun getPublicKey(): UByteArray {
        return keyPair.publicKey
    }

    fun getPrivateKey(): UByteArray {
        return keyPair.secretKey
    }
}
