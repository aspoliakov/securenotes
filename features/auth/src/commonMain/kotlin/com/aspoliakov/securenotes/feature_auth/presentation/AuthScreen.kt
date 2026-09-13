package com.aspoliakov.securenotes.feature_auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.aspoliakov.securenotes.core_ui.component.Spacer8dp
import com.aspoliakov.securenotes.core_ui.component.Spacer16dp
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
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AuthHeader()
            Spacer(modifier = Modifier.height(40.dp))
            AuthFormCard(
                    modifier = Modifier.fillMaxWidth(),
                    state = state,
                    intentHandler = intentHandler,
            )
            Spacer(modifier = Modifier.height(24.dp))
            SwitchAuthTypeAction(
                    state = state,
                    intentHandler = intentHandler,
            )
        }
    }
}

@Composable
internal fun AuthHeader(
        modifier: Modifier = Modifier,
) {
    Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(LocalCustomColorSchemeProvider.current.logoBackground)
                    .padding(22.dp),
                painter = painterResource(Res.drawable.app_logo_auth),
                contentDescription = stringResource(Res.string.app_name),
        )
        Spacer16dp()
        Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer8dp()
        Text(
                text = stringResource(Res.string.feature_about_tagline),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun AuthFormCard(
        modifier: Modifier = Modifier,
        state: AuthState,
        intentHandler: (AuthIntent) -> Unit = {},
) {
    val titleRes = when (state.authType) {
        AuthType.SIGN_IN -> Res.string.feature_auth_sign_in
        AuthType.SIGN_UP -> Res.string.feature_auth_sign_up
    }
    Column(
            modifier = modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(24.dp),
    ) {
        Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(20.dp))
        LoginTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                onValueChanged = { intentHandler(AuthIntent.OnEmailChanged(it)) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                password = state.password,
                onValueChanged = { intentHandler(AuthIntent.OnPasswordChanged(it)) },
                labelStringRes = Res.string.feature_auth_password_hint,
                errorStringRes = (state.authActionState as? AuthActionState.Error)?.error?.res,
        )
        Spacer(modifier = Modifier.height(24.dp))
        SignInSignOutActionView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                state = state,
                intentHandler = intentHandler,
        )
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
            label = { Text(text = stringResource(Res.string.feature_auth_email_hint)) },
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
    if (state.authActionState !is AuthActionState.Completed) {
        ButtonWithLoader(
                modifier = modifier,
                onClick = { intentHandler.invoke(AuthIntent.OnNextClick) },
                isLoading = state.authActionState is AuthActionState.Loading,
                stringResource = authActionButtonText,
        )
    }
}

@Composable
internal fun SwitchAuthTypeAction(
        modifier: Modifier = Modifier,
        state: AuthState,
        intentHandler: (AuthIntent) -> Unit = {},
) {
    if (state.authActionState is AuthActionState.Idle || state.authActionState is AuthActionState.Error) {
        val switchAuthTypeButtonText = when (state.authType) {
            AuthType.SIGN_IN -> Res.string.feature_auth_sign_up_suggest
            AuthType.SIGN_UP -> Res.string.feature_auth_sign_up_back_to_sign_in
        }
        Text(
                modifier = modifier.clickable {
                    intentHandler.invoke(AuthIntent.OnSwitchSignInSignUpClick)
                },
                text = stringResource(switchAuthTypeButtonText),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
        )
    }
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

@Preview
@Composable
private fun AuthScreenSignUpPreview() {
    AppTheme {
        AuthScreen(
                state = AuthState(
                        authType = AuthType.SIGN_UP,
                ),
        )
    }
}
