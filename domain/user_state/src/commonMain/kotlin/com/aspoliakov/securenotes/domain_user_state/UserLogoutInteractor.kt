package com.aspoliakov.securenotes.domain_user_state

/**
 * Project SecureNotes
 */

class UserLogoutInteractor(
        private val userStateInteractor: UserStateInteractor,
) {

    suspend fun logout() {
        userStateInteractor.clearUserState()
    }
}
