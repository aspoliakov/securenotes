package com.aspoliakov.securenotes.feature_auth.presentation.auth_providers

/**
 * Project SecureNotes
 */
interface GoogleSignInLauncher {

    fun signIn(
            onIdTokenReceived: (idToken: String) -> Unit,
            onCancelled: () -> Unit,
            onError: () -> Unit,
    )
}
