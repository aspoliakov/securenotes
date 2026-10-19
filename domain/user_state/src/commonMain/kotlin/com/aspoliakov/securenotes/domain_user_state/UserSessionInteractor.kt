package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_key_value_storage.EncryptedKeyValueStorage
import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import kotlinx.coroutines.flow.first

/**
 * Project SecureNotes
 */
class UserSessionInteractor(
        private val encryptedKeyValueStorage: EncryptedKeyValueStorage,
        private val keyValueStorage: KeyValueStorage,
) {

    companion object {
        private const val ACCESS_TOKEN = "token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val SESSION_ID = "session_id"
    }

    fun getAccessToken(): String? {
        return encryptedKeyValueStorage.getString(ACCESS_TOKEN)
    }

    fun getRefreshToken(): String? {
        return encryptedKeyValueStorage.getString(REFRESH_TOKEN)
    }

    suspend fun getSessionId(): String? {
        return keyValueStorage.getString(SESSION_ID).first()
    }

    suspend fun saveSession(
            accessToken: String,
            refreshToken: String,
            sessionId: String,
    ) {
        encryptedKeyValueStorage.put(ACCESS_TOKEN, accessToken)
        encryptedKeyValueStorage.put(REFRESH_TOKEN, refreshToken)
        keyValueStorage.put(SESSION_ID, sessionId)
    }

    fun saveTokens(
            accessToken: String,
            refreshToken: String,
    ) {
        encryptedKeyValueStorage.put(ACCESS_TOKEN, accessToken)
        encryptedKeyValueStorage.put(REFRESH_TOKEN, refreshToken)
    }
}
