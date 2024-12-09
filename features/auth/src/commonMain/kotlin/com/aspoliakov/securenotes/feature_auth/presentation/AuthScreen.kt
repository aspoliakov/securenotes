package com.aspoliakov.securenotes.feature_auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.LocalCustomColorSchemeProvider
import com.aspoliakov.securenotes.core_ui.component.ButtonWithLoader
import com.aspoliakov.securenotes.core_ui.component.PasswordTextField
import com.aspoliakov.securenotes.core_ui.resources.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
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
                    snackbarHostState.showSnackbar("error")
                }
            }
        }.collect()
    }

    Scaffold(
            modifier = modifier,
            snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        Column(
                modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                    modifier = Modifier
                            .weight(3f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                        modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(45.dp))
                                .background(LocalCustomColorSchemeProvider.current.logoBackground)
                                .padding(20.dp),
                        painter = painterResource(Res.drawable.app_logo_auth),
                        contentDescription = stringResource(Res.string.app_name),
                )
            }
            Column(
                    modifier = Modifier
                            .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LoginTextField(
                        value = state.email,
                        onValueChanged = { intentHandler(AuthIntent.OnEmailChanged(it)) },
                )
                PasswordTextField(
                        modifier = Modifier
                                .padding(top = 8.dp),
                        password = state.password,
                        onValueChanged = { intentHandler(AuthIntent.OnPasswordChanged(it)) },
                        errorStringRes = (state.authActionState as? AuthActionState.Error)?.error?.res,
                )
            }
            Column(
                    modifier = Modifier
                            .weight(3f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SignInSignOutActionView(
                        state = state,
                        intentHandler = intentHandler,
                )
            }
        }
    }
}

@Composable
internal fun LoginTextField(
        modifier: Modifier = Modifier,
        value: String,
        onValueChanged: (String) -> Unit,
) {
    OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChanged,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
            ),
            keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
            ),
            singleLine = true,
    )
}

@Composable
internal fun SignInSignOutActionView(
        modifier: Modifier = Modifier,
        state: AuthState,
        intentHandler: (AuthIntent) -> Unit = {},
) {
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

@Composable
internal fun AuthErrorText(
        authError: AuthError,
) {
    Text(
            text = stringResource(authError.res),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.error,
    )
}
