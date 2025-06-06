package com.aspoliakov.securenotes.feature_keys.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.common_error_network
import com.aspoliakov.securenotes.core_ui.resources.common_unexpected_error
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_restore_decryption_error
import org.jetbrains.compose.resources.StringResource

/**
 * Project SecureNotes
 */

sealed class KeysState : State() {
    data object Loading : KeysState()

    data class LoadingFailed(val keysError: KeysError) : KeysState()

    data class Creating(
            val password: String = "",
            val passwordRequirements: PasswordRequirements = PasswordRequirements(),
            val actionState: KeysActionState = KeysActionState.Idle,
    ) : KeysState() {
        data class PasswordRequirements(
                val maxLength: Boolean = false,
                val oneDigit: Boolean = false,
                val oneLetter: Boolean = false,
                val oneCapitalLetter: Boolean = false,
        ) {
            fun isSatisfied(): Boolean {
                return maxLength && oneDigit && oneLetter && oneCapitalLetter
            }
        }
    }

    data class Restoring(
            val keyId: String,
            val publicKey: String,
            val encryptedPrivateKey: String,
            val password: String = "",
            val actionState: KeysActionState = KeysActionState.Idle,
    ) : KeysState()
}

sealed class KeysActionState {
    data object Idle : KeysActionState()
    data object Loading : KeysActionState()
    data class Error(val error: KeysError) : KeysActionState()
    data object Completed : KeysActionState()
}

enum class KeysError(val res: StringResource) {
    NETWORK_ERROR(Res.string.common_error_network),
    UNEXPECTED_ERROR(Res.string.common_unexpected_error),
    DECRYPTION_ERROR(Res.string.feature_keys_restore_decryption_error),
}

sealed class KeysEffect : Effect() {
    data class ShowError(val keysError: KeysError) : KeysEffect()
}

sealed class KeysIntent : Intent() {
    data object OnBackClick : KeysIntent()
    data object OnReloadKeysClick : KeysIntent()
    data class OnPasswordChanged(val password: String) : KeysIntent()
    data object OnResetPasswordClick : KeysIntent()
    data object OnApplyClick : KeysIntent()
}
