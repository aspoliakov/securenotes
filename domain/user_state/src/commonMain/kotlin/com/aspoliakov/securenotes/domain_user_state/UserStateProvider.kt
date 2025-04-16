package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor.Companion.USER_AUTH_STATE
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor.Companion.USER_EMAIL
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor.Companion.USER_ID
import com.aspoliakov.securenotes.domain_user_state.model.UserProfileData
import com.aspoliakov.securenotes.domain_user_state.model.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Project SecureNotes
 */

class UserStateProvider(
        private val keyValueStorage: KeyValueStorage,
) {

    suspend fun getUid(): String? {
        return keyValueStorage.getString(USER_ID).firstOrNull()
    }

    suspend fun getUserState(): UserState {
        return observeUserState().first()
    }

    fun observeUserState(): Flow<UserState> {
        return keyValueStorage.getInt(USER_AUTH_STATE)
                .map { it?.let(UserState::fromIntState) ?: UserState.UNAUTHORIZED }
    }

    suspend fun getUserProfileData(): UserProfileData {
        val name = keyValueStorage.getString(USER_EMAIL).firstOrNull()
        return UserProfileData(
                displayName = name ?: "",
        )
    }
}
