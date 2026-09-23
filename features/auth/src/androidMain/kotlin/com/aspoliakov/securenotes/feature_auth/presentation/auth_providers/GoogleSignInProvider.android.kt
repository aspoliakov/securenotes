package com.aspoliakov.securenotes.feature_auth.presentation.auth_providers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.aspoliakov.securenotes.core_base.GOOGLE_WEB_CLIENT_ID
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return {
        scope.launch {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(GOOGLE_WEB_CLIENT_ID)
                    .build()
            val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
            try {
                val result = credentialManager.getCredential(context, request)
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                onIdTokenReceived(googleIdTokenCredential.idToken)
            } catch (e: GetCredentialCancellationException) {
                onCancelled()
            } catch (e: GetCredentialException) {
                onError()
            }
        }
    }
}
