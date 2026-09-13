package com.aspoliakov.securenotes.feature_keys.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.component.ButtonWithLoader
import com.aspoliakov.securenotes.core_ui.component.PasswordTextField
import com.aspoliakov.securenotes.core_ui.component.TopAppBar
import com.aspoliakov.securenotes.core_ui.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
internal fun KeysRestoringView(
        modifier: Modifier,
        state: KeysState.Restoring,
        intentHandler: (KeysIntent) -> Unit = {},
) {
    Scaffold(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                        onBackClick = { intentHandler.invoke(KeysIntent.OnBackClick) },
                )
            },
    ) { paddings ->
        var openResetKeysDialog by remember { mutableStateOf(false) }
        if (openResetKeysDialog) {
            ResetPasswordDialog(
                    onDismiss = { openResetKeysDialog = false },
                    onConfirm = {
                        openResetKeysDialog = false
                        intentHandler.invoke(KeysIntent.OnResetPasswordClick)
                    },
            )
        }
        Column(
                modifier = Modifier
                    .padding(paddings)
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            KeysHeader(
                    titleRes = Res.string.feature_keys_restore_title,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(24.dp),
            ) {
                PasswordTextField(
                        modifier = Modifier.fillMaxWidth(),
                        password = state.password,
                        onValueChanged = { intentHandler(KeysIntent.OnPasswordChanged(it)) },
                        labelStringRes = Res.string.feature_auth_password_hint,
                        errorStringRes = (state.actionState as? KeysActionState.Error)?.error?.res,
                )
                if (state.actionState !is KeysActionState.Completed) {
                    Spacer(modifier = Modifier.height(24.dp))
                    ButtonWithLoader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            onClick = { intentHandler.invoke(KeysIntent.OnApplyClick) },
                            isLoading = state.actionState is KeysActionState.Loading,
                            stringResource = Res.string.common_apply,
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                    modifier = Modifier.clickable { openResetKeysDialog = true },
                    text = stringResource(Res.string.feature_keys_restore_forgot_password),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
internal fun ResetPasswordDialog(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit,
) {
    var confirmEnabled by remember { mutableStateOf(false) }
    AlertDialog(
            icon = {
                Icon(
                        imageVector = Icons.ResetKeys,
                        contentDescription = stringResource(Res.string.feature_keys_restore_reset_keys_title),
                )
            },
            title = {
                Text(text = stringResource(Res.string.feature_keys_restore_reset_keys_title))
            },
            text = {
                Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                ) {
                    Text(
                            text = buildAnnotatedString {
                                append(stringResource(Res.string.feature_keys_restore_reset_keys_disclaimer_part_1))
                                withStyle(
                                        style = SpanStyle(
                                                color = MaterialTheme.colorScheme.error,
                                                fontWeight = FontWeight.Medium,
                                        )
                                ) {
                                    append(stringResource(Res.string.feature_keys_restore_reset_keys_disclaimer_part_2))
                                }
                            }
                    )
                    Row(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .clickable { confirmEnabled = !confirmEnabled },

                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                            Checkbox(
                                    checked = confirmEnabled,
                                    onCheckedChange = { confirmEnabled = it },
                            )
                        }
                        Text(
                                modifier = Modifier
                                    .padding(start = 12.dp),
                                text = stringResource(Res.string.feature_keys_restore_reset_keys_confirm),
                        )
                    }
                }
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                        enabled = confirmEnabled,
                        onClick = onConfirm,
                ) {
                    Text(
                            text = stringResource(Res.string.common_confirm),
                            fontWeight = FontWeight.Medium,
                    )
                }
            },
            dismissButton = {
                TextButton(
                        onClick = onDismiss,
                ) {
                    Text(stringResource(Res.string.common_cancel))
                }
            }
    )
}
