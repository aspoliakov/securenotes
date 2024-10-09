package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.component.Spacer8dp
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.feature_notes_add_note
import com.aspoliakov.securenotes.core_ui.resources.notes
import com.aspoliakov.securenotes.domain_notes.data.NotesListItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * Project SecureNotes
 */

@Composable
fun NotesBrowserScreenRoute(
        modifier: Modifier = Modifier,
) {
    val viewModel: NotesBrowserViewModel = koinInject()
    val state = viewModel.currentState
    NotesBrowserScreen(
            modifier = modifier,
            state = state,
            intentHandler = viewModel::handleIntent,
    )
}

@Composable
internal fun NotesBrowserScreen(
        modifier: Modifier = Modifier,
        state: NotesBrowserState = NotesBrowserState.Init,
        intentHandler: (NotesBrowserIntent) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    BoxWithConstraints(
            modifier = Modifier
                    .fillMaxSize()
    ) {
        when (state) {
            is NotesBrowserState.Init -> NotesInitView()
            is NotesBrowserState.Loaded -> NotesListView(state.notesList)
        }
        ExtendedFloatingActionButton(
                modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                onClick = { intentHandler.invoke(NotesBrowserIntent.CreateNote) },
                icon = {
                    Icon(
                            painter = painterResource(Res.drawable.notes),
                            contentDescription = stringResource(Res.string.feature_notes_add_note),
                    )
                },
                text = {
                    Text(text = stringResource(Res.string.feature_notes_add_note))
                },
        )
    }
}

@Composable
internal fun NotesInitView() {
    Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
    ) {
        Text(
                text = "Loading...",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
internal fun NotesListView(
        notesList: List<NotesListItem>,
) {
    LazyColumn {
        items(notesList) {
            Spacer8dp()
            NotesListItem(it)
        }
    }
}

@Composable
internal fun NotesListItem(noteListItem: NotesListItem) {
    Box(
            modifier = Modifier
                    .fillMaxWidth()
                    .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp),
                    )
                    .padding(32.dp)
    ) {
        Text(
                text = noteListItem.body,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
        )
    }
}
