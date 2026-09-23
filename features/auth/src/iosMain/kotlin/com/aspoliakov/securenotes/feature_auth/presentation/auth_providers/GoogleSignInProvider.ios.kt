package com.aspoliakov.securenotes.feature_auth.presentation.auth_providers

import androidx.compose.runtime.Composable

/**
 * Project SecureNotes
 */

actual val isGoogleSignInSupported: Boolean = false

@Composable
internal actual fun rememberGoogleSignInLauncher(
        onIdTokenReceived: (idToken: String) -> Unit,
        onCancelled: () -> Unit,
        onError: () -> Unit,
): () -> Unit {
    return {}
}
