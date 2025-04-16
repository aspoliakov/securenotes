package com.aspoliakov.securenotes.domain_user_state.model

/**
 * Project SecureNotes
 */

enum class AuthResult {
    OK,
    SIGN_IN_WRONG_CREDENTIALS,
    SIGN_UP_USER_ALREADY_REGISTERERD,
    NETWORK_ERROR,
    UNEXPECTED_ERROR,
}
