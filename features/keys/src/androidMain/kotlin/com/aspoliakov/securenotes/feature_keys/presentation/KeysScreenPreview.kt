package com.aspoliakov.securenotes.feature_keys.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.aspoliakov.securenotes.core_ui.AppTheme

/**
 * Project SecureNotes
 */

@Preview
@Composable
fun KeysScreenPreview() {
    AppTheme {
        KeysScreen(
                state = KeysState.Restoring(
                        keyId = "key_id",
                        publicKey = "public_key",
                        encryptedPrivateKey = "encrypted_private_key",
                        password = "Psd",
                        actionState = KeysActionState.Error(KeysError.DECRYPTION_ERROR),
                ),
        )
    }
}
