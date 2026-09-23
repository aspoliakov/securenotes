package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponseException
import com.aspoliakov.securenotes.domain_user_state.model.*
import com.aspoliakov.securenotes.domain_user_state.network.AuthApiProvider
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 */

class UserAuthInteractor(
        private val userStateInteractor: UserStateInteractor,
        private val authApiProvider: AuthApiProvider,
) {

    suspend fun signIn(
            email: String,
            password: String,
    ): AuthResult {
        return runCatching {
            val response = authApiProvider.provideApi().authenticate(
                    request = AuthEmailRequest(
                            email = email,
                            password = password,
                    )
            )
            userStateInteractor.setUserAuthorized(
                    userId = response.user.userId,
                    email = response.user.email,
                    token = response.token,
            )
            AuthResult.OK
        }.getOrElse(this::onAuthFailure)
    }

    suspend fun signInWithGoogle(idToken: String): AuthResult {
        return runCatching {
            val response = authApiProvider.provideApi().authenticateViaGoogle(
                    request = AuthGoogleRequest(
                            idToken = idToken,
                    ),
            )
            userStateInteractor.setUserAuthorized(
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
            userStateInteractor.setUserAuthorized(
                    userId = response.user.userId,
                    email = response.user.email,
                    token = response.token,
            )
            AuthResult.OK
        }.getOrElse(this::onAuthFailure)
    }

    private fun onAuthFailure(throwable: Throwable): AuthResult {
        Napier.e("Auth error: $throwable")
        return if (throwable is ErrorResponseException) {
            when (throwable.detail) {
                AuthEmailResponse.ERROR_WRONG_CREDENTIALS ->
                    AuthResult.SIGN_IN_WRONG_CREDENTIALS
                AuthGoogleResponse.ERROR_EMAIL_REGISTERED_WITH_PASSWORD ->
                    AuthResult.SIGN_IN_GOOGLE_EMAIL_REGISTERED_WITH_PASSWORD
                RegisterResponse.ERROR_USER_ALREADY_REGISTERERD ->
                    AuthResult.SIGN_UP_USER_ALREADY_REGISTERERD
                else -> AuthResult.UNEXPECTED_ERROR
            }
        } else {
            AuthResult.UNEXPECTED_ERROR
        }
    }
}
