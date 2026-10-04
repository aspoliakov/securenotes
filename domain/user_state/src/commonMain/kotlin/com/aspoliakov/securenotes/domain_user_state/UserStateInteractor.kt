package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_db.DatabaseManager
import com.aspoliakov.securenotes.core_key_value_storage.EncryptedKeyValueStorage
import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import com.aspoliakov.securenotes.domain_user_state.model.UserState

/**
 * Project SecureNotes
 */

class UserStateInteractor(
        private val keyValueStorage: KeyValueStorage,
        private val encryptedKeyValueStorage: EncryptedKeyValueStorage,
        private val databaseManager: DatabaseManager,
) {

    companion object {
        const val USER_AUTH_STATE = "user_auth_state"
        const val USER_EMAIL = "user_email"
        const val USER_ID = "user_id"

        const val USER_TOKEN = "token"
    }

    suspend fun setUserActive() {
        keyValueStorage.put(USER_AUTH_STATE, UserState.ACTIVE.state)
    }

    fun getUserToken(): String? {
        return encryptedKeyValueStorage.getString(USER_TOKEN)
    }

    suspend fun setUserAuthorized(
            userId: String,
            email: String,
            token: String,
    ) {
        keyValueStorage.put(USER_EMAIL, email)
        keyValueStorage.put(USER_ID, userId)
        keyValueStorage.put(USER_AUTH_STATE, UserState.AUTHORIZED.state)
        encryptedKeyValueStorage.put(USER_TOKEN, token)
    }

    suspend fun clearUserState() {
        databaseManager.clearAll()
        encryptedKeyValueStorage.clear()
        keyValueStorage.clear()
    }
}
