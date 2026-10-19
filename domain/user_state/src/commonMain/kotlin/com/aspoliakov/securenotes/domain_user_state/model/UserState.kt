package com.aspoliakov.securenotes.domain_user_state.model

/**
 * Project SecureNotes
 */

enum class UserState(val state: Int) {
    AUTH(1),
    KEYS(2),
    ACTIVE(3),
    DEACTIVATED(4);

    companion object {
        fun fromIntState(state: Int): UserState {
            return entries.firstOrNull { it.state == state } ?: AUTH
        }
    }

    fun isAuthorized(): Boolean {
        return when (this) {
            KEYS,
            ACTIVE -> true
            AUTH,
            DEACTIVATED -> false
        }
    }
}
