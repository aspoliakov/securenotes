package com.aspoliakov.securenotes.feature_note.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.feature_note_text_field_body_hint
import com.aspoliakov.securenotes.core_ui.resources.feature_note_text_field_title_hint
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Project SecureNotes
 */

@Composable
fun NoteScreenRoute(
        modifier: Modifier = Modifier,
        onNavigationBack: () -> Unit,
        noteId: String?,
) {
    val viewModel = koinViewModel<NoteViewModel>()
    val state = viewModel.currentState
    NoteScreen(
            modifier = modifier,
            onNavigationBack = onNavigationBack,
            state = state,
            intentHandler = viewModel::handleIntent,
    )
}

@Composable
internal fun NoteScreen(
        modifier: Modifier = Modifier,
        onNavigationBack: () -> Unit,
        state: NoteState = NoteState(),
        intentHandler: (NoteIntent) -> Unit = {},
) {
    Scaffold(
            topBar = {
                NoteToolbar(
                        onNavigationBack = onNavigationBack,
                )
            }
    ) { padding ->
        Column(
                modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 4.dp),
        ) {
            NoteTitle(
                    value = state.title,
                    onValueChange = { intentHandler(NoteIntent.OnTitleChanged(it)) },
            )
            NoteBody(
                    value = state.body,
                    onValueChange = { intentHandler(NoteIntent.OnBodyChanged(it)) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteToolbar(
        modifier: Modifier = Modifier,
        onNavigationBack: () -> Unit,
) {
    TopAppBar(
            navigationIcon = {
                IconButton(
                        onClick = { onNavigationBack() },
                ) {
                    Icon(
                            imageVector = Icons.ArrowBack,
                            contentDescription = null,
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
internal fun NoteTitle(
        value: String,
        onValueChange: (String) -> Unit,
) {
    val style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Normal,
    )
    TextField(
            modifier = Modifier
                    .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            textStyle = style,
            placeholder = {
                Text(
                        style = style.copy(
                                color = style.color.copy(alpha = 0.4F),
                        ),
                        text = stringResource(Res.string.feature_note_text_field_title_hint)
                )
            },
            colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
            ),
    )
}

@Composable
internal fun NoteBody(
        value: String,
        onValueChange: (String) -> Unit,
) {
    val style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Normal,
    )
    TextField(
            modifier = Modifier
                    .fillMaxSize(),
            value = value,
            onValueChange = onValueChange,
            textStyle = style,
            placeholder = {
                Text(
                        style = style.copy(
                                color = style.color.copy(alpha = 0.4F),
                        ),
                        text = stringResource(Res.string.feature_note_text_field_body_hint)
                )
            },
            colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
            ),
    )
}
