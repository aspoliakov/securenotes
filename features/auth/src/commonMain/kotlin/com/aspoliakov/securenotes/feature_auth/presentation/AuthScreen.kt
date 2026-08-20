package com.aspoliakov.securenotes.feature_auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.LocalCustomColorSchemeProvider
import com.aspoliakov.securenotes.core_ui.component.ButtonWithLoader
import com.aspoliakov.securenotes.core_ui.component.PasswordTextField
import com.aspoliakov.securenotes.core_ui.resources.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
fun AuthScreenRoute(
        modifier: Modifier = Modifier,
) {
    val viewModel = koinMviViewModel<AuthViewModel>()
    val state by viewModel.state.collectAsState()
    AuthScreen(
            modifier = modifier,
            state = state,
            effects = viewModel.effects,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun AuthScreen(
        modifier: Modifier = Modifier,
        state: AuthState = AuthState(),
        effects: Flow<Effect> = emptyFlow(),
        intentHandler: (AuthIntent) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    CollectEffects<AuthEffect>(effects) { effect ->
        when (effect) {
            is AuthEffect.ShowSnackbar -> scope.launch {
                snackbarHostState.showSnackbar("error")
            }
        }
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
                        .weight(2f),
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
                    verticalArrangement = Arrangement.Top,
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
                        .weight(2f),
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

@Preview
@Composable
private fun AuthScreenPreview() {
    AppTheme {
        AuthScreen(
                state = AuthState(
                        email = "test@email.com",
                        password = "password",
                        authActionState = AuthActionState.Error(
                                error = AuthError.UNEXPECTED_ERROR,
                        ),
                        authType = AuthType.SIGN_IN,
                ),
        )
    }
}
