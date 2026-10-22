package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_base.AppConfigProvider
import com.aspoliakov.securenotes.core_network.auth.AuthTokenProvider
import com.aspoliakov.securenotes.core_network.exceptions.ErrorResponseException
import com.aspoliakov.securenotes.domain_user_state.model.RefreshTokenRequest
import com.aspoliakov.securenotes.domain_user_state.network.AuthApiProvider
import io.github.aakira.napier.Napier
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Project SecureNotes
 */
class RefreshTokenInteractor(
        private val appConfigProvider: AppConfigProvider,
        private val userSessionInteractor: UserSessionInteractor,
        private val userLogoutInteractor: UserLogoutInteractor,
        private val authApiProvider: AuthApiProvider,
) : AuthTokenProvider {

    companion object {
        private const val HTTP_UNAUTHORIZED = 401
    }

    private val refreshMutex = Mutex()

    override suspend fun getAccessToken(): String? {
        val accessToken = userSessionInteractor.getAccessToken()
        if (accessToken == null) {
            expireSession()
        }
        return accessToken
    }

    override suspend fun refreshAccessToken(rejectedAccessToken: String): String? {
        return refreshMutex.withLock {
            val currentAccessToken = userSessionInteractor.getAccessToken()
            if (currentAccessToken == null) {
                expireSession()
                return@withLock null
            }
            if (currentAccessToken != rejectedAccessToken) {
                // a concurrent request has already refreshed it
                return@withLock currentAccessToken
            }
            val refreshToken = userSessionInteractor.getRefreshToken()
            if (refreshToken == null) {
                expireSession()
                return@withLock null
            }
            refreshTokens(
                    rejectedAccessToken = rejectedAccessToken,
                    refreshToken = refreshToken,
            )
        }
    }

    private suspend fun refreshTokens(
            rejectedAccessToken: String,
            refreshToken: String,
    ): String? {
        // once the server rotates the refresh token, the old one is dead: the new pair must be saved
        // even if the request that triggered the refresh gets cancelled meanwhile
        return withContext(NonCancellable) {
            val response = runCatching {
                authApiProvider.provideApi().refreshToken(
                        request = RefreshTokenRequest(
                                refreshToken = refreshToken,
                                appVersion = appConfigProvider.getVersionName(),
                        ),
                )
            }.getOrElse { throwable ->
                Napier.e("Token refresh error: $throwable")
                if (throwable is CancellationException) throw throwable
                if (throwable.isSessionRejected()) expireSession()
                return@withContext null
            }
            if (userSessionInteractor.getAccessToken() != rejectedAccessToken) {
                // signed out while the refresh was in flight
                return@withContext null
            }
            userSessionInteractor.saveTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
            )
            response.accessToken
        }
    }

    private suspend fun expireSession() {
        Napier.e("Session expired, signing out")
        userLogoutInteractor.deactivate()
    }

    private fun Throwable.isSessionRejected(): Boolean {
        return this is ErrorResponseException && statusCode == HTTP_UNAUTHORIZED
    }
}
