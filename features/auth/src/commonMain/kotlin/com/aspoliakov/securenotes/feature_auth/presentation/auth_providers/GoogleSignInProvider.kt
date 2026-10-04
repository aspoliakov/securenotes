package com.aspoliakov.securenotes.feature_auth.presentation.auth_providers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Project SecureNotes
 */

expect val isGoogleSignInSupported: Boolean

@Composable
internal expect fun rememberGoogleSignInLauncher(
        onIdTokenReceived: (idToken: String) -> Unit,
        onCancelled: () -> Unit,
        onError: () -> Unit,
): () -> Unit

@Composable
fun GoogleSignInProvider(
        shouldLaunch: Boolean,
        onIdTokenReceived: (idToken: String) -> Unit,
        onCancelled: () -> Unit,
        onError: () -> Unit,
) {
    val launchGoogleSignIn = rememberGoogleSignInLauncher(
            onIdTokenReceived = onIdTokenReceived,
            onCancelled = onCancelled,
            onError = onError,
    )
    LaunchedEffect(shouldLaunch) {
        if (shouldLaunch) {
            launchGoogleSignIn()
        }
    }
}
