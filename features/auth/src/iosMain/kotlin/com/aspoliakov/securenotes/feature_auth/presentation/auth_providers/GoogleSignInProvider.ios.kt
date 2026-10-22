package com.aspoliakov.securenotes.feature_auth.presentation.auth_providers

import androidx.compose.runtime.Composable
import com.aspoliakov.securenotes.core_base.GOOGLE_WEB_CLIENT_ID
import com.aspoliakov.securenotes.core_base.GoogleSignInLauncher
import org.koin.compose.koinInject

/**
 * Project SecureNotes
 */

actual val isGoogleSignInSupported: Boolean = true

@Composable
internal actual fun rememberGoogleSignInLauncher(
        onIdTokenReceived: (idToken: String) -> Unit,
        onCancelled: () -> Unit,
        onError: () -> Unit,
): () -> Unit {
    val googleSignInLauncher = koinInject<GoogleSignInLauncher>()
    return {
        googleSignInLauncher.signIn(
                serverClientId = GOOGLE_WEB_CLIENT_ID,
                onIdTokenReceived = onIdTokenReceived,
                onCancelled = onCancelled,
                onError = onError,
        )
    }
}
