package com.aspoliakov.securenotes.feature_auth.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

/**
 * Project SecureNotes
 */

data class AuthState(
        val email: String = "",
        val password: String = "",
        val authActionState: AuthActionState = AuthActionState.Idle,
        val authType: AuthType = AuthType.SIGN_IN,
) : MviState()

enum class AuthType {
    SIGN_IN,
    SIGN_UP,
}

sealed class AuthActionState {
    data object Idle : AuthActionState()
    data object Loading : AuthActionState()
    data class Error(val error: AuthError) : AuthActionState()
    data object Completed : AuthActionState()
}

enum class AuthError {
    WRONG_EMAIL,
    PASSWORD_IS_EMPTY,
    WEAK_PASSWORD,
    WRONG_CREDENTIALS,
    USER_ALREADY_REGISTERED,
    NETWORK_ERROR,
    UNEXPECTED_ERROR,
}

sealed class AuthEffect : MviEffect() {
    data class ShowSnackbar(val stringRes: Int) : AuthEffect()
}

sealed class AuthIntent : MviIntent() {
    data class OnEmailChanged(val email: String) : AuthIntent()
    data class OnPasswordChanged(val password: String) : AuthIntent()
    data object OnSwitchSignInSignUpClick : AuthIntent()
    data object OnNextClick : AuthIntent()
}
