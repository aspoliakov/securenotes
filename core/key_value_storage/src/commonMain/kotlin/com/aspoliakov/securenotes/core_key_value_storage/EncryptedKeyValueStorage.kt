package com.aspoliakov.securenotes.core_key_value_storage

import com.liftric.kvault.KVault

/**
 * Project SecureNotes
 */

class EncryptedKeyValueStorage(
        private val store: KVault,
) {

    fun put(key: String, value: String) {
        store.set(key, value)
    }

    fun getString(key: String): String? {
        return store.string(key)
    }

    fun clear() {
        store.clear()
    }
}
