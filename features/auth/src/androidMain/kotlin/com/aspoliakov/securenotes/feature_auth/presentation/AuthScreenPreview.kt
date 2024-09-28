package com.aspoliakov.securenotes.feature_auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.aspoliakov.securenotes.core_ui.AppTheme

/**
 * Project SecureNotes
 */

@Preview
@Composable
fun AuthScreenPreview() {
    AppTheme {
        AuthScreen(
                state = AuthState(
                        email = "test@email.com",
                        password = "password",
                        authActionState = AuthActionState.Idle,
                        authType = AuthType.SIGN_IN,
                )
        )
    }
}
