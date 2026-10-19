package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_db.DatabaseManager
import com.aspoliakov.securenotes.core_key_value_storage.EncryptedKeyValueStorage
import com.aspoliakov.securenotes.domain_user_state.model.LogoutRequest
import com.aspoliakov.securenotes.domain_user_state.network.AuthApiProvider
import io.github.aakira.napier.Napier
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * Project SecureNotes
 */

class UserLogoutInteractor(
        private val userStateInteractor: UserStateInteractor,
        private val userStateProvider: UserStateProvider,
        private val userSessionInteractor: UserSessionInteractor,
        private val encryptedKeyValueStorage: EncryptedKeyValueStorage,
        private val databaseManager: DatabaseManager,
        private val authApiProvider: AuthApiProvider,
) {

    companion object {
        private const val REVOKE_SESSION_TIMEOUT = 5000L
    }

    private val logoutMutex = Mutex()

    suspend fun logout() {
        withContext(NonCancellable) {
            logoutMutex.withLock {
                revokeSession()
                clearUserData()
            }
        }
    }

    suspend fun deactivate() {
        withContext(NonCancellable) {
            logoutMutex.withLock {
                // already signed out: by the user meanwhile or by a concurrent deactivation
                if (!userStateProvider.isAuthorized()) return@withLock
                clearUserData()
                userStateInteractor.setUserDeactivated()
            }
        }
    }

    private suspend fun revokeSession() {
        val refreshToken = userSessionInteractor.getRefreshToken() ?: return
        runCatching {
            withTimeout(REVOKE_SESSION_TIMEOUT.milliseconds) {
                authApiProvider.provideApi().logout(
                        request = LogoutRequest(refreshToken = refreshToken),
                )
            }
        }.onFailure {
            Napier.e("Error revoking user session: $it")
        }
    }

    private suspend fun clearUserData() {
        databaseManager.clearAll()
        encryptedKeyValueStorage.clear()
        userStateInteractor.clearUserState()
    }
}
