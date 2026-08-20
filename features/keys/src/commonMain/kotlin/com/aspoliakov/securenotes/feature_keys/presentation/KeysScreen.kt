package com.aspoliakov.securenotes.feature_keys.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.common_retry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
fun KeysScreenRoute(
        modifier: Modifier = Modifier,
) {
    val viewModel = koinMviViewModel<KeysViewModel>()
    val state by viewModel.state.collectAsState()
    KeysScreen(
            modifier = modifier,
            state = state,
            effects = viewModel.effects,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun KeysScreen(
        modifier: Modifier = Modifier,
        state: KeysState = KeysState.Loading,
        effects: Flow<Effect> = emptyFlow(),
        intentHandler: (KeysIntent) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    CollectEffects<KeysEffect>(effects) { effect ->
        when (effect) {
            is KeysEffect.ShowError -> {}
        }
    }
    Scaffold(
            modifier = modifier,
            snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddings ->
        when (state) {
            is KeysState.Loading -> KeysLoadingState(
                    modifier = modifier.padding(paddings),
            )
            is KeysState.LoadingFailed -> KeysLoadingFailedState(
                    modifier = modifier.padding(paddings),
                    state = state,
                    intentHandler = intentHandler,
            )
            is KeysState.Creating -> KeysCreatingView(
                    modifier = modifier.padding(paddings),
                    state = state,
                    intentHandler = intentHandler,
            )
            is KeysState.Restoring -> KeysRestoring(
                    modifier = modifier.padding(paddings),
                    state = state,
                    intentHandler = intentHandler,
            )
        }
    }
}

@Composable
internal fun KeysLoadingState(
        modifier: Modifier,
) {
    Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
internal fun KeysLoadingFailedState(
        modifier: Modifier,
        state: KeysState.LoadingFailed,
        intentHandler: (KeysIntent) -> Unit = {},
) {
    Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
    ) {
        KeysErrorText(
                keysError = state.keysError,
        )
        Button(
                modifier = Modifier
                    .padding(12.dp),
                shape = ButtonDefaults.textShape,
                onClick = { intentHandler.invoke(KeysIntent.OnReloadKeysClick) },
        ) {
            Text(
                    text = stringResource(Res.string.common_retry),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
internal fun KeysErrorText(
        keysError: KeysError,
) {
    Text(
            text = stringResource(keysError.res),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.error,
    )
}

@Preview
@Composable
private fun KeysScreenPreview() {
    AppTheme {
        KeysScreen(
                state = KeysState.Restoring(
                        keyId = "key_id",
                        publicKey = "public_key",
                        encryptedPrivateKey = "encrypted_private_key",
                        password = "password",
                        actionState = KeysActionState.Error(KeysError.DECRYPTION_ERROR),
                ),
        )
    }
}
