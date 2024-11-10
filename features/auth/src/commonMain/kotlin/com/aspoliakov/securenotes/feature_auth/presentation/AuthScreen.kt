package com.aspoliakov.securenotes.feature_auth.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.component.ButtonWithLoader
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.app_name
import com.aspoliakov.securenotes.core_ui.resources.common_error_network
import com.aspoliakov.securenotes.core_ui.resources.common_unexpected_error
import com.aspoliakov.securenotes.core_ui.resources.feature_auth_error_empty_password
import com.aspoliakov.securenotes.core_ui.resources.feature_auth_error_user_already_registered
import com.aspoliakov.securenotes.core_ui.resources.feature_auth_error_weak_password
import com.aspoliakov.securenotes.core_ui.resources.feature_auth_error_wrong_credentials
import com.aspoliakov.securenotes.core_ui.resources.feature_auth_error_wrong_email
import com.aspoliakov.securenotes.core_ui.resources.feature_auth_sign_in
import com.aspoliakov.securenotes.core_ui.resources.feature_auth_sign_up
import com.aspoliakov.securenotes.core_ui.resources.feature_auth_sign_up_back_to_sign_in
import com.aspoliakov.securenotes.core_ui.resources.feature_auth_sign_up_suggest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Project SecureNotes
 */

@Composable
fun AuthScreenRoute(
        modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val state = viewModel.currentState
    AuthScreen(
            modifier = modifier,
            state = state,
            effects = viewModel.effects.filterIsInstance(),
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun AuthScreen(
        modifier: Modifier = Modifier,
        state: AuthState = AuthState(),
        effects: Flow<AuthEffect> = emptyFlow(),
        intentHandler: (AuthIntent) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(effects) {
        effects.onEach { effect ->
            when (effect) {
                is AuthEffect.ShowSnackbar -> scope.launch {
//                    snackbarHostState.showSnackbar(stringResource(effect.stringRes))
                }
            }
        }.collect()
    }

    Scaffold(
            modifier = modifier,
            snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddings ->
        Column(
                modifier = modifier
                        .padding(paddings)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
        ) {
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.weight(1F),
            ) {
                Text(
                        text = stringResource(Res.string.app_name),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                )
            }
            Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier.weight(2F),
            ) {
                TextField(
                        value = state.email,
                        onValueChange = { intentHandler(AuthIntent.OnEmailChanged(it)) },
                )
                TextField(
                        value = state.password,
                        onValueChange = { intentHandler(AuthIntent.OnPasswordChanged(it)) },
                )
                Spacer(modifier = modifier.height(60.dp))
                if (state.authActionState is AuthActionState.Error) {
                    AuthErrorText(
                            authError = state.authActionState.error,
                    )
                    Spacer(modifier = modifier.height(30.dp))
                }
                val authActionButtonText = when (state.authType) {
                    AuthType.SIGN_IN -> Res.string.feature_auth_sign_in
                    AuthType.SIGN_UP -> Res.string.feature_auth_sign_up
                }
                val switchAuthTypeButtonText = when (state.authType) {
                    AuthType.SIGN_IN -> Res.string.feature_auth_sign_up_suggest
                    AuthType.SIGN_UP -> Res.string.feature_auth_sign_up_back_to_sign_in
                }
                if (state.authActionState !is AuthActionState.Completed) {
                    ButtonWithLoader(
                            onClick = { intentHandler.invoke(AuthIntent.OnNextClick) },
                            isLoading = state.authActionState is AuthActionState.Loading,
                            stringResource = authActionButtonText,
                    )
                }
                if (state.authActionState is AuthActionState.Idle || state.authActionState is AuthActionState.Error) {
                    Spacer(modifier = modifier.height(60.dp))
                    Text(
                            modifier = modifier.clickable {
                                intentHandler.invoke(AuthIntent.OnSwitchSignInSignUpClick)
                            },
                            text = stringResource(switchAuthTypeButtonText),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
internal fun AuthErrorText(
        authError: AuthError,
) {
    val text = when (authError) {
        AuthError.WRONG_EMAIL -> Res.string.feature_auth_error_wrong_email
        AuthError.PASSWORD_IS_EMPTY -> Res.string.feature_auth_error_empty_password
        AuthError.WEAK_PASSWORD -> Res.string.feature_auth_error_weak_password
        AuthError.WRONG_CREDENTIALS -> Res.string.feature_auth_error_wrong_credentials
        AuthError.USER_ALREADY_REGISTERED -> Res.string.feature_auth_error_user_already_registered
        AuthError.NETWORK_ERROR -> Res.string.common_error_network
        AuthError.UNEXPECTED_ERROR -> Res.string.common_unexpected_error
    }
    Text(
            text = stringResource(text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.errorContainer,
    )
}
