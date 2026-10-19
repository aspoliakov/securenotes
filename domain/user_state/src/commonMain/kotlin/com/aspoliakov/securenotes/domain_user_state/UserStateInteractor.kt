package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import com.aspoliakov.securenotes.domain_user_state.model.UserState

/**
 * Project SecureNotes
 */
class UserStateInteractor(
        private val keyValueStorage: KeyValueStorage,
) {

    companion object {
        const val USER_AUTH_STATE = "user_auth_state"
        const val USER_EMAIL = "user_email"
        const val USER_ID = "user_id"
    }

    suspend fun setUserAuthorized(
            userId: String,
            email: String,
    ) {
        keyValueStorage.put(USER_EMAIL, email)
        keyValueStorage.put(USER_ID, userId)
        keyValueStorage.put(USER_AUTH_STATE, UserState.KEYS.state)
    }

    suspend fun setUserActive() {
        keyValueStorage.put(USER_AUTH_STATE, UserState.ACTIVE.state)
    }

    suspend fun setUserDeactivated() {
        keyValueStorage.put(USER_AUTH_STATE, UserState.DEACTIVATED.state)
    }
}
