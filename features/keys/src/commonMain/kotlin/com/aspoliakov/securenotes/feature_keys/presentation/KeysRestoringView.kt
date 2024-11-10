package com.aspoliakov.securenotes.feature_keys.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.component.ButtonWithLoader
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.common_apply
import com.aspoliakov.securenotes.core_ui.resources.common_back
import com.aspoliakov.securenotes.core_ui.resources.common_cancel
import com.aspoliakov.securenotes.core_ui.resources.common_confirm
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_restore_forgot_password
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_restore_reset_keys_confirm
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_restore_reset_keys_disclaimer_part_1
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_restore_reset_keys_disclaimer_part_2
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_restore_reset_keys_title
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_restore_title
import com.aspoliakov.securenotes.core_ui.resources.feature_keys_title
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
internal fun KeysRestoring(
        modifier: Modifier,
        state: KeysState.Restoring,
        intentHandler: (KeysIntent) -> Unit = {},
) {
    Scaffold(
            topBar = {
                KeysRestoringToolbar(
                        onBackClick = { intentHandler.invoke(KeysIntent.OnBackClick) },
                )
            }
    ) {
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
                modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                    modifier = Modifier
                            .weight(3F),
                    verticalArrangement = Arrangement.Center,
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
                        text = stringResource(Res.string.feature_keys_restore_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                )
            }
            Column(
                    modifier = Modifier
                            .weight(1F),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PasswordTextField(
                        modifier = Modifier
                                .fillMaxWidth(),
                        password = state.password,
                        onValueChanged = { intentHandler(KeysIntent.OnPasswordChanged(it)) },
                        errorStringRes = (state.actionState as? KeysActionState.Error)?.error?.res,
                )
            }
            Column(
                    modifier = Modifier
                            .weight(0.3F),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton(
                        contentPadding = PaddingValues(),
                        onClick = { openResetKeysDialog = true },
                ) {
                    Text(
                            text = stringResource(Res.string.feature_keys_restore_forgot_password),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Normal,
                    )
                }
            }
            Column(
                    modifier = Modifier
                            .weight(3F),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeysRestoringToolbar(
        modifier: Modifier = Modifier,
        onBackClick: () -> Unit,
) {
    TopAppBar(
            modifier = modifier,
            navigationIcon = {
                IconButton(
                        onClick = { onBackClick() },
                ) {
                    Icon(
                            imageVector = Icons.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            title = {
                Text(
                        text = "",
                        fontWeight = FontWeight.Normal,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                )
            },
    )
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
