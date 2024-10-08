package com.aspoliakov.securenotes.domain_user_state.model

/**
 * Project SecureNotes
 */

enum class UserState(val state: Int) {
    UNAUTHORIZED(1),
    AUTHORIZED(2);

    companion object {
        fun fromIntState(state: Int): UserState {
            return values().firstOrNull { it.state == state } ?: UNAUTHORIZED
        }
    }
}
