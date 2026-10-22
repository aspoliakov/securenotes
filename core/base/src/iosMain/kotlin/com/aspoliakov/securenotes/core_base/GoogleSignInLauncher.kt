package com.aspoliakov.securenotes.core_base

/**
 * Project SecureNotes
 *
 * Implemented by the iOS app: the GoogleSignIn SDK is a Swift package, so it can't be called from Kotlin directly.
 * Callbacks are invoked on the main thread.
 */
interface GoogleSignInLauncher {

    fun signIn(
            serverClientId: String,
            onIdTokenReceived: (idToken: String) -> Unit,
            onCancelled: () -> Unit,
            onError: () -> Unit,
    )
}
