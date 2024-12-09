package com.aspoliakov.securenotes.feature_keys.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.component.ButtonWithLoader
import com.aspoliakov.securenotes.core_ui.component.PasswordTextField
import com.aspoliakov.securenotes.core_ui.resources.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
internal fun KeysCreatingView(
        modifier: Modifier = Modifier,
        state: KeysState.Creating,
        effects: Flow<KeysEffect>,
        intentHandler: (KeysIntent) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(effects) {
        effects.onEach { effect ->
            when (effect) {
                is KeysEffect.ShowError -> scope.launch {
                    snackbarHostState.showSnackbar("error") // TODO
                }
            }
        }.collect()
    }
    Column(
            modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
                modifier = Modifier
                        .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                    modifier = Modifier
                            .padding(top = 40.dp)
                            .size(40.dp),
                    imageVector = Icons.Security,
                    contentDescription = stringResource(Res.string.feature_keys_title),
                    tint = MaterialTheme.colorScheme.secondary,
            )
            Text(
                    modifier = Modifier
                            .padding(top = 40.dp),
                    text = stringResource(
                            Res.string.feature_keys_title,
                            stringResource(Res.string.app_name),
                    ),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
            )
            Text(
                    modifier = Modifier
                            .padding(top = 40.dp),
                    text = stringResource(Res.string.feature_keys_subtitle),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
            )
        }
        Column(
                modifier = Modifier
                        .padding(top = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PasswordTextField(
                    modifier = Modifier
                            .fillMaxWidth(),
                    password = state.password,
                    onValueChanged = { intentHandler(KeysIntent.OnPasswordChanged(it)) },
                    errorStringRes = (state.actionState as? KeysActionState.Error)?.error?.res,
            )
            PasswordRequirementsView(state)
        }
        Column(
                modifier = Modifier
                        .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.actionState !is KeysActionState.Completed) {
                ButtonWithLoader(
                        onClick = { intentHandler.invoke(KeysIntent.OnApplyClick) },
                        isLoading = state.actionState is KeysActionState.Loading,
                        stringResource = Res.string.common_apply,
                )
            }
        }
    }
}

@Composable
internal fun PasswordRequirementsView(state: KeysState.Creating) {
    Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                            horizontal = 4.dp,
                            vertical = 12.dp,
                    ),
            horizontalAlignment = Alignment.Start,
    ) {
        PasswordRequirementItem(
                modifier = Modifier
                        .padding(top = 12.dp),
                text = Res.string.feature_keys_password_requirement_length,
                success = state.passwordRequirements.maxLength,
        )
        PasswordRequirementItem(
                modifier = Modifier
                        .padding(top = 16.dp),
                text = Res.string.feature_keys_password_requirement_digit,
                success = state.passwordRequirements.oneDigit,
        )
        PasswordRequirementItem(
                modifier = Modifier
                        .padding(top = 16.dp),
                text = Res.string.feature_keys_password_requirement_letter,
                success = state.passwordRequirements.oneLetter,
        )
        PasswordRequirementItem(
                modifier = Modifier
                        .padding(top = 16.dp),
                text = Res.string.feature_keys_password_requirement_capital_letter,
                success = state.passwordRequirements.oneCapitalLetter,
        )
    }
}

@Composable
internal fun PasswordRequirementItem(
        modifier: Modifier = Modifier,
        text: StringResource,
        success: Boolean,
) {
    Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
                modifier = Modifier
                        .size(16.dp),
                imageVector = if (success) Icons.Checked else Icons.Unchecked,
                contentDescription = stringResource(text),
                tint = MaterialTheme.colorScheme.secondary,
        )
        Text(
                modifier = Modifier
                        .padding(start = 6.dp),
                text = stringResource(text),
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light,
        )
    }
}
