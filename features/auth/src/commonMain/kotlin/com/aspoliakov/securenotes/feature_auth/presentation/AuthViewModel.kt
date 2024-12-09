package com.aspoliakov.securenotes.feature_auth.presentation

import com.aspoliakov.securenotes.core_base.util.Patterns
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import com.aspoliakov.securenotes.domain_user_state.model.AuthResult
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 */

class AuthViewModel(
        initialState: AuthState,
        private val userStateInteractor: UserStateInteractor,
) : MviViewModel<AuthState, AuthEffect, AuthIntent>(initialState) {

    override fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.OnEmailChanged -> reduceState {
                currentState.copy(
                        email = intent.email,
                        authActionState = AuthActionState.Idle,
                )
            }
            is AuthIntent.OnPasswordChanged -> reduceState {
                currentState.copy(
                        password = intent.password,
                        authActionState = AuthActionState.Idle,
                )
            }
            is AuthIntent.OnSwitchSignInSignUpClick -> onSwitchSignInSignUpClick()
            is AuthIntent.OnNextClick -> onNextClick()
        }
    }

    private fun onSwitchSignInSignUpClick() {
        reduceState {
            copy(
                    authType = when (currentState.authType) {
                        AuthType.SIGN_IN -> AuthType.SIGN_UP
                        AuthType.SIGN_UP -> AuthType.SIGN_IN
                    },
                    authActionState = AuthActionState.Idle,
            )
        }
    }

    @Suppress("MaxLineLength")
    private fun onNextClick() = launchOnIO {
        val email = currentState.email.trim()
        val password = currentState.password
        when {
            email.isBlank() || !Patterns.checkEmailIsValid(email) -> {
                reduceState { currentState.copy(authActionState = AuthActionState.Error(AuthError.WRONG_EMAIL)) }
            }
            password.isBlank() -> {
                reduceState { currentState.copy(authActionState = AuthActionState.Error(AuthError.PASSWORD_IS_EMPTY)) }
            }
            else -> {
                reduceState { currentState.copy(authActionState = AuthActionState.Loading) }
                val result = when (currentState.authType) {
                    AuthType.SIGN_IN -> userStateInteractor.signIn(email = email, password = password)
                    AuthType.SIGN_UP -> userStateInteractor.signUp(email = email, password = password)
                }
                Napier.d("Result: $result")
                val authActionState = when (result) {
                    AuthResult.OK,
                    AuthResult.SIGN_OUT -> AuthActionState.Completed
                    AuthResult.SIGN_IN_WRONG_CREDENTIALS -> AuthActionState.Error(AuthError.WRONG_CREDENTIALS)
                    AuthResult.SIGN_UP_WEAK_PASSWORD -> AuthActionState.Error(AuthError.WEAK_PASSWORD)
                    AuthResult.SIGN_UP_USER_ALREADY_REGISTERERD -> AuthActionState.Error(AuthError.USER_ALREADY_REGISTERED)
                    AuthResult.NETWORK_ERROR -> AuthActionState.Error(AuthError.NETWORK_ERROR)
                    AuthResult.UNEXPECTED_ERROR -> AuthActionState.Error(AuthError.UNEXPECTED_ERROR)
                }
                reduceState { currentState.copy(authActionState = authActionState) }
            }
        }
    }
}
