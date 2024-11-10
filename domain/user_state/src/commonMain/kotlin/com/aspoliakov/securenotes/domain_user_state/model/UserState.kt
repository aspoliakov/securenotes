package com.aspoliakov.securenotes.domain_user_state.model

/**
 * Project SecureNotes
 */

enum class UserState(val state: Int) {
    UNAUTHORIZED(1),
    AUTHORIZED(2),
    ACTIVE(3);

    companion object {
        fun fromIntState(state: Int): UserState {
            return entries.firstOrNull { it.state == state } ?: UNAUTHORIZED
        }
    }
}
