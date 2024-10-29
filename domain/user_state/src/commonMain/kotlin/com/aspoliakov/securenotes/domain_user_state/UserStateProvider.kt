package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor.Companion.USER_AUTH_STATE
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor.Companion.USER_EMAIL
import com.aspoliakov.securenotes.domain_user_state.model.UserProfileData
import com.aspoliakov.securenotes.domain_user_state.model.UserState
import dev.gitlive.firebase.auth.FirebaseAuth
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map

/**
 * Project SecureNotes
 */

class UserStateProvider(
        private val keyValueStorage: KeyValueStorage,
        private val auth: FirebaseAuth,
) {

    fun getUid(): String? {
        return auth.currentUser?.uid
    }

    fun getUserState(): Flow<UserState> {
        return keyValueStorage.getInt(USER_AUTH_STATE)
                .map { it?.let(UserState::fromIntState) ?: UserState.UNAUTHORIZED }
    }

    suspend fun getUserProfileData(): UserProfileData {
        Napier.d("Current user: ${auth.currentUser?.email}")
        val name = auth.currentUser?.email ?: keyValueStorage.getString(USER_EMAIL).last()
        return UserProfileData(
                displayName = name ?: "",
        )
    }
}
