package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.component.Spacer4dp
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
    NotesBrowserScreen(
            modifier = modifier,
            state = viewModel.currentState,
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
    Column {
        SearchView(
                modifier = modifier
                        .fillMaxWidth(),
        )
        BoxWithConstraints(
                modifier = Modifier
                        .fillMaxSize()
        ) {
            when (state) {
                is NotesBrowserState.Init -> NotesInitView()
                is NotesBrowserState.Loaded -> NotesList(state.notesList)
            }
            FloatingButton(
                    modifier = modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                    intentHandler = intentHandler,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchView(
        modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

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
internal fun NotesList(
        notesList: List<NotesListItem>,
) {
    // TODO: common list with last item spacer as param
    LazyColumn {
        items(notesList) {
            Spacer8dp()
            NotesListItem(it)
            if (it == notesList.last()) {
                Spacer8dp()
            }
        }
    }
}

@Composable
internal fun NotesListItem(noteListItem: NotesListItem) {
    Column(
            modifier = Modifier
                    .padding(
                            start = 8.dp,
                            end = 8.dp,
                    )
                    .fillMaxWidth()
                    .clip(
                            shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(4.dp),
                    )
                    .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                    )
                    .padding(8.dp),
    ) {
        Spacer4dp()
        val title = noteListItem.title
        if (title != null) {
            Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
            )
            Spacer8dp()
        }
        Text(
                text = noteListItem.body,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Thin,
        )
    }
}

@Composable
internal fun FloatingButton(
        modifier: Modifier,
        intentHandler: (NotesBrowserIntent) -> Unit,
) {
    ExtendedFloatingActionButton(
            modifier = modifier,
            onClick = { intentHandler.invoke(NotesBrowserIntent.CreateNote) },
            icon = {
                Icon(
                        painter = painterResource(Res.drawable.notes),
                        contentDescription = stringResource(Res.string.feature_notes_add_note),
                )
            },
            text = {
                Text(
                        text = stringResource(Res.string.feature_notes_add_note)
                )
            },
    )
}
