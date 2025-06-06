package com.aspoliakov.securenotes.feature_keys.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_crypto.KeysCreateResult
import com.aspoliakov.securenotes.domain_crypto.KeysRestoreResult
import com.aspoliakov.securenotes.domain_crypto.UserKeysInteractor
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 */

class KeysViewModel(
        initialState: KeysState,
        private val userKeysInteractor: UserKeysInteractor,
        private val userStateInteractor: UserStateInteractor,
) : MviViewModel<KeysState, KeysEffect, KeysIntent>(initialState) {

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val MAX_PASSWORD_LENGTH = 128
    }

    override fun initData() {
        loadKeys()
    }

    override fun handleIntent(intent: KeysIntent) {
        when (intent) {
            is KeysIntent.OnBackClick -> onBackClick()
            is KeysIntent.OnReloadKeysClick -> loadKeys()
            is KeysIntent.OnPasswordChanged -> onPasswordChanged(intent.password)
            is KeysIntent.OnResetPasswordClick -> onResetPasswordClick()
            is KeysIntent.OnApplyClick -> onApplyClick()
        }
    }

    private fun onBackClick() = launchOnIO {
        userStateInteractor.logout()
    }

    private fun loadKeys() = launchOnIO {
        reduceState { KeysState.Loading }
        runCatching {
            val keyPair = userKeysInteractor.loadKey()
            if (keyPair == null) {
                reduceState { KeysState.Creating() }
            } else {
                reduceState {
                    KeysState.Restoring(
                            keyId = keyPair.keyId,
                            publicKey = keyPair.publicKey,
                            encryptedPrivateKey = keyPair.encryptedPrivateKey,
                    )
                }
            }
        }.onFailure {
            Napier.e("Load keys error: $it")
            reduceState {
                KeysState.LoadingFailed(
                        keysError = KeysError.NETWORK_ERROR,
                )
            }
        }
    }

    private fun onPasswordChanged(password: String) {
        when (val state = currentState) {
            is KeysState.Creating -> {
                val passwordRequirements = checkPasswordRequirements(password)
                reduceState {
                    state.copy(
                            password = password,
                            passwordRequirements = passwordRequirements,
                            actionState = KeysActionState.Idle,
                    )
                }
            }
            is KeysState.Restoring -> {
                reduceState {
                    state.copy(
                            password = password,
                            actionState = KeysActionState.Idle,
                    )
                }
            }
            else -> return
        }
    }

    private fun onResetPasswordClick() {
        reduceState { KeysState.Creating() }
    }

    private fun checkPasswordRequirements(password: String): KeysState.Creating.PasswordRequirements {
        return KeysState.Creating.PasswordRequirements(
                maxLength = password.length in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH,
                oneDigit = password.matches("^.*\\d+.*$".toRegex()),
                oneLetter = password.matches("^.*\\p{Ll}.*$".toRegex()),
                oneCapitalLetter = password.matches("^.*\\p{Lu}.*$".toRegex()),
        )
    }

    private fun onApplyClick() {
        when (val state = currentState) {
            is KeysState.Creating -> createNewUserKey(state)
            is KeysState.Restoring -> restoreUserKey(state)
            else -> return
        }
    }

    private fun createNewUserKey(state: KeysState.Creating) = launchOnIO {
        if (!state.passwordRequirements.isSatisfied()) return@launchOnIO
        reduceState { state.copy(actionState = KeysActionState.Loading) }
        val result = userKeysInteractor.createKey(state.password)
        if (result == KeysCreateResult.OK) {
            reduceState { state.copy(actionState = KeysActionState.Completed) }
        } else {
            val keysError = when (result) {
                KeysCreateResult.NETWORK_ERROR -> KeysError.NETWORK_ERROR
                KeysCreateResult.UNEXPECTED_ERROR -> KeysError.UNEXPECTED_ERROR
                KeysCreateResult.OK -> KeysError.UNEXPECTED_ERROR
            }
            sendEffect { KeysEffect.ShowError(keysError) }
            reduceState { state.copy(actionState = KeysActionState.Idle) }
        }
    }

    private fun restoreUserKey(state: KeysState.Restoring) = launchOnIO {
        reduceState { state.copy(actionState = KeysActionState.Loading) }
        val result = userKeysInteractor.restoreKey(
                password = state.password,
                keyId = state.keyId,
                publicKey = state.publicKey,
                encryptedPrivateKey = state.encryptedPrivateKey,
        )
        val actionState = when (result) {
            KeysRestoreResult.OK -> KeysActionState.Completed
            KeysRestoreResult.FAILED -> KeysActionState.Error(KeysError.DECRYPTION_ERROR)
        }
        reduceState { state.copy(actionState = actionState) }
    }
}
