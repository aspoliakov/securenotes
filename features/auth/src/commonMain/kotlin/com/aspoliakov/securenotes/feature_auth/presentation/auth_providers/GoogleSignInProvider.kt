package com.aspoliakov.securenotes.feature_auth.presentation.auth_providers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.koin.compose.koinInject

/**
 * Project SecureNotes
 */

@Composable
fun GoogleSignInProvider(
        shouldLaunch: Boolean,
        onIdTokenReceived: (idToken: String) -> Unit,
        onCancelled: () -> Unit,
        onError: () -> Unit,
) {
    val googleSignInLauncher = koinInject<GoogleSignInLauncher>()
    LaunchedEffect(shouldLaunch) {
        if (shouldLaunch) {
            googleSignInLauncher.signIn(
                    onIdTokenReceived = onIdTokenReceived,
                    onCancelled = onCancelled,
                    onError = onError,
            )
        }
    }
}
