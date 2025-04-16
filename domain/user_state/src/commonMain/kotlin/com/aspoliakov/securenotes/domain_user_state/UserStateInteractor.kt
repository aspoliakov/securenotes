package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_db.DatabaseManager
import com.aspoliakov.securenotes.core_key_value_storage.EncryptedKeyValueStorage
import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponseException
import com.aspoliakov.securenotes.domain_user_state.model.*
import com.aspoliakov.securenotes.domain_user_state.network.AuthApiProvider
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 */

class UserStateInteractor(
        private val keyValueStorage: KeyValueStorage,
        private val encryptedKeyValueStorage: EncryptedKeyValueStorage,
        private val authApiProvider: AuthApiProvider,
        private val databaseManager: DatabaseManager,
) {

    companion object {
        const val USER_AUTH_STATE = "user_auth_state"
        const val USER_EMAIL = "user_email"
        const val USER_ID = "user_id"

        const val USER_TOKEN = "token"
    }

    suspend fun signIn(
            email: String,
            password: String,
    ): AuthResult {
        return runCatching {
            val response = authApiProvider.provideApi().authenticate(
                    request = AuthenticateRequest(
                            email = email,
                            password = password,
                    )
            )
            setUserAuthorized(
                    userId = response.user.userId,
                    email = response.user.email,
                    token = response.token,
            )
            AuthResult.OK
        }.getOrElse(this::onAuthFailure)
    }

    suspend fun signUp(
            email: String,
            password: String,
    ): AuthResult {
        return runCatching {
            val response = authApiProvider.provideApi().register(
                    request = RegisterRequest(
                            email = email,
                            password = password,
                    )
            )
            setUserAuthorized(
                    userId = response.user.userId,
                    email = response.user.email,
                    token = response.token,
            )
            AuthResult.OK
        }.getOrElse(this::onAuthFailure)
    }

    suspend fun logout() {
        databaseManager.clearAll()
        encryptedKeyValueStorage.clear()
        keyValueStorage.clear()
    }

    suspend fun setUserActive() {
        keyValueStorage.put(USER_AUTH_STATE, UserState.ACTIVE.state)
    }

    fun getUserToken(): String? {
        return encryptedKeyValueStorage.getString(USER_TOKEN)
    }

    private fun onAuthFailure(throwable: Throwable): AuthResult {
        Napier.e("Auth error: $throwable")
        return if (throwable is ErrorResponseException) {
            when (throwable.detail) {
                AuthenticateResponse.ERROR_WRONG_CREDENTIALS -> AuthResult.SIGN_IN_WRONG_CREDENTIALS
                RegisterResponse.ERROR_USER_ALREADY_REGISTERERD -> AuthResult.SIGN_UP_USER_ALREADY_REGISTERERD
                else -> AuthResult.UNEXPECTED_ERROR
            }
        } else {
            AuthResult.UNEXPECTED_ERROR
        }
    }

    private suspend fun setUserAuthorized(
            userId: String,
            email: String,
            token: String,
    ) {
        keyValueStorage.put(USER_EMAIL, email)
        keyValueStorage.put(USER_ID, userId)
        keyValueStorage.put(USER_AUTH_STATE, UserState.AUTHORIZED.state)
        encryptedKeyValueStorage.put(USER_TOKEN, token)
    }
}
