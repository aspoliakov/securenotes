package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_network.exceptions.Error404Exception
import com.aspoliakov.securenotes.domain_user_state.model.ActiveSession
import com.aspoliakov.securenotes.domain_user_state.model.SessionPlatform
import com.aspoliakov.securenotes.domain_user_state.model.UserSessionDTO
import com.aspoliakov.securenotes.domain_user_state.network.UserSessionsApiProvider
import io.github.aakira.napier.Napier
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Instant

/**
 * Project SecureNotes
 */
class ActiveSessionsInteractor(
        private val userSessionInteractor: UserSessionInteractor,
        private val userSessionsApiProvider: UserSessionsApiProvider,
) {

    companion object {
        private const val PLATFORM_ANDROID = "android"
        private const val PLATFORM_IOS = "ios"
    }

    suspend fun getSessions(): Result<List<ActiveSession>> {
        return runSessionsCatching {
            val currentSessionId = userSessionInteractor.getSessionId()
            userSessionsApiProvider.provideApi().getSessions()
                .map { it.toActiveSession(currentSessionId) }
                .sortedWith(
                        compareByDescending<ActiveSession> { it.isCurrent }
                            .thenByDescending { it.lastActiveAt }
                )
        }
    }

    suspend fun terminateSession(sessionId: String): Result<Unit> {
        return runSessionsCatching {
            try {
                userSessionsApiProvider.provideApi().terminateSession(sessionId)
            } catch (_: Error404Exception) {
                // already gone: expired or terminated from another device
            }
        }
    }

    suspend fun terminateOtherSessions(): Result<Unit> {
        return runSessionsCatching {
            userSessionsApiProvider.provideApi().terminateOtherSessions()
        }
    }

    private fun UserSessionDTO.toActiveSession(currentSessionId: String?): ActiveSession {
        return ActiveSession(
                id = sessionId,
                deviceName = deviceName,
                platform = when (platform) {
                    PLATFORM_ANDROID -> SessionPlatform.ANDROID
                    PLATFORM_IOS -> SessionPlatform.IOS
                    else -> SessionPlatform.UNKNOWN
                },
                osVersion = osVersion,
                appVersion = appVersion,
                createdAt = Instant.parse(createdAt),
                lastActiveAt = Instant.parse(lastActiveAt),
                isCurrent = sessionId == currentSessionId,
        )
    }

    private inline fun <T> runSessionsCatching(block: () -> T): Result<T> {
        return runCatching(block).onFailure { throwable ->
            if (throwable is CancellationException) throw throwable
            Napier.e("Active sessions error: $throwable")
        }
    }
}
