package com.aspoliakov.securenotes.feature_auth.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State
import com.aspoliakov.securenotes.core_ui.resources.*
import org.jetbrains.compose.resources.StringResource

/**
 * Project SecureNotes
 */

data class AuthState(
        val email: String = "",
        val password: String = "",
        val authActionState: AuthActionState = AuthActionState.Idle,
        val authType: AuthType = AuthType.SIGN_IN,
) : State()

enum class AuthType {
    SIGN_IN,
    SIGN_UP,
}

sealed class AuthActionState {
    data object Idle : AuthActionState()
    sealed class Active : AuthActionState() {
        data object Email : Active()
        data object Google : Active()
    }
    data class Error(val error: AuthError) : AuthActionState()
    data object Completed : AuthActionState()
}

enum class AuthError(val res: StringResource) {
    WRONG_EMAIL(Res.string.feature_auth_error_wrong_email),
    PASSWORD_IS_EMPTY(Res.string.feature_auth_error_empty_password),
    WRONG_CREDENTIALS(Res.string.feature_auth_error_wrong_credentials),
    USER_ALREADY_REGISTERED(Res.string.feature_auth_error_user_already_registered),
    GOOGLE_EMAIL_REGISTERED_WITH_PASSWORD(Res.string.feature_auth_error_google_email_registered_with_password),
    NETWORK_ERROR(Res.string.common_error_network),
    UNEXPECTED_ERROR(Res.string.common_unexpected_error),
}

sealed class AuthEffect : Effect() {
    data class ShowSnackbar(val stringRes: Int) : AuthEffect()
}

sealed class AuthIntent : Intent() {
    data class OnEmailChanged(val email: String) : AuthIntent()
    data class OnPasswordChanged(val password: String) : AuthIntent()
    data object OnSwitchSignInSignUpClick : AuthIntent()
    data object OnNextClick : AuthIntent()
    data object OnGoogleSignInClick : AuthIntent()
    data class OnGoogleIdTokenReceived(val idToken: String) : AuthIntent()
    data object OnGoogleSignInCancelled : AuthIntent()
    data object OnGoogleSignInFailed : AuthIntent()
}
