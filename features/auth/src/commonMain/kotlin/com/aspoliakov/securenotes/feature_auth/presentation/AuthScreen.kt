package com.aspoliakov.securenotes.feature_auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.aspoliakov.securenotes.core_ui.component.CenteredKeyboardAwareColumn
import com.aspoliakov.securenotes.core_ui.component.DividerWithText
import com.aspoliakov.securenotes.core_ui.component.PasswordTextField
import com.aspoliakov.securenotes.core_ui.resources.*
import com.aspoliakov.securenotes.feature_auth.presentation.auth_providers.GoogleSignInProvider
import com.aspoliakov.securenotes.feature_auth.presentation.auth_providers.isGoogleSignInSupported
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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
    val snackbarHostState = remember { SnackbarHostState() }
    val viewsEnabled = state.authActionState !is AuthActionState.Active

    CollectEffects<AuthEffect>(effects) { effect ->
        when (effect) {
            is AuthEffect.ShowSnackbar -> {}
        }
    }

    if (isGoogleSignInSupported) {
        GoogleSignInProvider(
                shouldLaunch = state.authActionState is AuthActionState.Active.Google,
                onIdTokenReceived = { intentHandler(AuthIntent.OnGoogleIdTokenReceived(it)) },
                onCancelled = { intentHandler(AuthIntent.OnGoogleSignInCancelled) },
                onError = { intentHandler(AuthIntent.OnGoogleSignInFailed) },
        )
    }

    Scaffold(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.systemBars,
            snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        CenteredKeyboardAwareColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(
                        horizontal = 24.dp,
                        vertical = 20.dp,
                ),
        ) {
            AuthHeader()
            Spacer(modifier = Modifier.height(24.dp))
            AuthFormCard(
                    modifier = Modifier.fillMaxWidth(),
                    state = state,
                    viewsEnabled = viewsEnabled,
                    intentHandler = intentHandler,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 36.dp),
                    text = (state.authActionState as? AuthActionState.Error)?.error?.let {
                        stringResource(it.res)
                    }.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SwitchAuthTypeAction(
                    state = state,
                    enabled = viewsEnabled,
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
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(LocalCustomColorSchemeProvider.current.logoBackground)
                    .padding(16.dp),
                painter = painterResource(Res.drawable.ic_app_logo_auth),
                contentDescription = stringResource(Res.string.app_name),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
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
        viewsEnabled: Boolean,
        intentHandler: (AuthIntent) -> Unit = {},
) {
    val titleRes = when (state.authType) {
        AuthType.SIGN_IN -> Res.string.feature_auth_sign_in
        AuthType.SIGN_UP -> Res.string.feature_auth_sign_up
    }
    val focusManager = LocalFocusManager.current
    Column(
            modifier = modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(20.dp),
    ) {
        Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        LoginTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                onValueChanged = { intentHandler(AuthIntent.OnEmailChanged(it)) },
                enabled = viewsEnabled,
                onImeAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
        )
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                password = state.password,
                onValueChanged = { intentHandler(AuthIntent.OnPasswordChanged(it)) },
                labelStringRes = Res.string.feature_auth_password_hint,
                enabled = viewsEnabled,
                onImeAction = {
                    focusManager.clearFocus()
                    intentHandler(AuthIntent.OnNextClick)
                },
        )
        Spacer(modifier = Modifier.height(16.dp))
        SignInSignOutActionView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                state = state,
                enabled = viewsEnabled,
                intentHandler = intentHandler,
        )
        if (isGoogleSignInSupported && state.authActionState !is AuthActionState.Completed) {
            Spacer(modifier = Modifier.height(16.dp))
            DividerWithText(textRes = Res.string.feature_auth_or_divider)
            Spacer(modifier = Modifier.height(16.dp))
            ButtonWithLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = { intentHandler(AuthIntent.OnGoogleSignInClick) },
                    isLoading = state.authActionState is AuthActionState.Active.Google,
                    enabled = viewsEnabled,
                    stringResource = Res.string.feature_auth_sign_in_with_google,
                    icon = {
                        Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(Res.drawable.ic_google_logo),
                                contentDescription = null,
                        )
                    },
            )
        }
    }
}

@Composable
internal fun LoginTextField(
        modifier: Modifier = Modifier,
        value: String,
        onValueChanged: (String) -> Unit,
        enabled: Boolean = true,
        onImeAction: () -> Unit = {},
) {
    OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChanged,
            enabled = enabled,
            label = { Text(text = stringResource(Res.string.feature_auth_email_hint)) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
            ),
            keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                    onNext = { onImeAction() },
            ),
            singleLine = true,
    )
}

@Composable
internal fun SignInSignOutActionView(
        modifier: Modifier = Modifier,
        state: AuthState,
        enabled: Boolean,
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
                isLoading = state.authActionState is AuthActionState.Active.Email,
                enabled = enabled,
                stringResource = authActionButtonText,
        )
    }
}

@Composable
internal fun SwitchAuthTypeAction(
        modifier: Modifier = Modifier,
        state: AuthState,
        enabled: Boolean,
        intentHandler: (AuthIntent) -> Unit = {},
) {
    val switchAuthTypeButtonText = when (state.authType) {
        AuthType.SIGN_IN -> Res.string.feature_auth_sign_up_suggest
        AuthType.SIGN_UP -> Res.string.feature_auth_sign_up_back_to_sign_in
    }
    Text(
            modifier = modifier
                .clickable(enabled = enabled) {
                    intentHandler.invoke(AuthIntent.OnSwitchSignInSignUpClick)
                }
                .alpha(if (enabled) 1f else 0.4f),
            text = stringResource(switchAuthTypeButtonText),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
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
