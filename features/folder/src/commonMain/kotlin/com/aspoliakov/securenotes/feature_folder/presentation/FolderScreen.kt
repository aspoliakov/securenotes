package com.aspoliakov.securenotes.feature_folder.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.component.Spacer16dp
import com.aspoliakov.securenotes.core_ui.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

/**
 * Project SecureNotes
 */

@Composable
fun FolderScreenRoute(
        modifier: Modifier = Modifier,
        mode: FolderMode,
        onDismiss: () -> Unit,
) {
    val viewModel = koinMviViewModel<FolderViewModel>(parameters = { parametersOf(mode) })
    val state by viewModel.state.collectAsState()
    CollectEffects<FolderEffect>(viewModel.effects) { effect ->
        when (effect) {
            is FolderEffect.Dismiss -> onDismiss()
        }
    }
    FolderScreen(
            modifier = modifier,
            state = state,
            onDismiss = onDismiss,
            intentHandler = viewModel::emitIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderScreen(
        modifier: Modifier = Modifier,
        state: FolderState,
        onDismiss: () -> Unit,
        intentHandler: (FolderIntent) -> Unit = {},
) {
    ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center,
                ) {
                    Icon(
                            imageVector = Icons.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                        text = when (state.mode) {
                            is FolderMode.Create -> stringResource(Res.string.feature_notes_new_folder)
                            is FolderMode.Edit -> stringResource(Res.string.feature_notes_rename_folder_title)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer16dp()
            OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.name,
                    onValueChange = { intentHandler(FolderIntent.OnNameChanged(it)) },
                    singleLine = true,
                    label = { Text(text = stringResource(Res.string.feature_notes_folder_name_hint)) },
            )
            Spacer16dp()
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { intentHandler(FolderIntent.OnCancelClick) }) {
                    Text(text = stringResource(Res.string.common_cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                        enabled = state.name.isNotBlank(),
                        onClick = { intentHandler(FolderIntent.OnConfirmClick) },
                ) {
                    Text(
                            text = when (state.mode) {
                                is FolderMode.Create -> stringResource(Res.string.common_create)
                                is FolderMode.Edit -> stringResource(Res.string.common_confirm)
                            }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun FolderScreenCreatePreview() {
    AppTheme {
        FolderScreen(
                state = FolderState(mode = FolderMode.Create(parentId = null)),
                onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun FolderScreenEditPreview() {
    AppTheme {
        FolderScreen(
                state = FolderState(mode = FolderMode.Edit(folderId = "1"), name = "Ideas"),
                onDismiss = {},
        )
    }
}
