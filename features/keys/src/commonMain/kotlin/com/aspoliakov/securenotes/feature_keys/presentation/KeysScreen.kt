package com.aspoliakov.securenotes.feature_keys.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.common_retry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.StringResource
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
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddings ->
        when (state) {
            is KeysState.Loading -> KeysLoadingView(
                    modifier = modifier.padding(paddings),
            )
            is KeysState.LoadingFailed -> KeysLoadingFailedView(
                    modifier = modifier.padding(paddings),
                    state = state,
                    intentHandler = intentHandler,
            )
            is KeysState.Creating -> KeysCreatingView(
                    modifier = modifier.padding(paddings),
                    state = state,
                    intentHandler = intentHandler,
            )
            is KeysState.Restoring -> KeysRestoringView(
                    modifier = modifier.padding(paddings),
                    state = state,
                    intentHandler = intentHandler,
            )
        }
    }
}

@Composable
internal fun KeysLoadingView(
        modifier: Modifier,
) {
    Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
internal fun KeysLoadingFailedView(
        modifier: Modifier,
        state: KeysState.LoadingFailed,
        intentHandler: (KeysIntent) -> Unit = {},
) {
    Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
    ) {
        KeysErrorText(
                keysError = state.keysError,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
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

@Composable
internal fun KeysHeader(
        modifier: Modifier = Modifier,
        titleRes: StringResource,
        titleArg: String? = null,
        subtitleRes: StringResource? = null,
) {
    Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
        ) {
            Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
                text = if (titleArg != null) {
                    stringResource(titleRes, titleArg)
                } else {
                    stringResource(titleRes)
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
        )
        if (subtitleRes != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    text = stringResource(subtitleRes),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun KeysScreenRestoringPreview() {
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

@Preview
@Composable
private fun KeysScreenCreatingPreview() {
    AppTheme {
        KeysScreen(
                state = KeysState.Creating(
                        password = "Password1",
                        passwordRequirements = KeysState.Creating.PasswordRequirements(
                                maxLength = true,
                                oneDigit = true,
                                oneLetter = true,
                                oneCapitalLetter = false,
                        ),
                ),
        )
    }
}
