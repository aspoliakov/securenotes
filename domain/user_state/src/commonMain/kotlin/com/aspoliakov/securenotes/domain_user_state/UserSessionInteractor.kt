package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_key_value_storage.EncryptedKeyValueStorage

/**
 * Project SecureNotes
 */
class UserSessionInteractor(
        private val encryptedKeyValueStorage: EncryptedKeyValueStorage,
) {

    companion object {
        private const val ACCESS_TOKEN = "token"
        private const val REFRESH_TOKEN = "refresh_token"
    }

    fun getAccessToken(): String? {
        return encryptedKeyValueStorage.getString(ACCESS_TOKEN)
    }

    fun getRefreshToken(): String? {
        return encryptedKeyValueStorage.getString(REFRESH_TOKEN)
    }

    fun saveSession(
            accessToken: String,
            refreshToken: String,
    ) {
        encryptedKeyValueStorage.put(ACCESS_TOKEN, accessToken)
        encryptedKeyValueStorage.put(REFRESH_TOKEN, refreshToken)
    }
}
