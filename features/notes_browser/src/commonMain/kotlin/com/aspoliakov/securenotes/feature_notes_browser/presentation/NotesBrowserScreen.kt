package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.component.Spacer12dp
import com.aspoliakov.securenotes.core_ui.component.Spacer4dp
import com.aspoliakov.securenotes.core_ui.resources.Res
import com.aspoliakov.securenotes.core_ui.resources.common_back
import com.aspoliakov.securenotes.core_ui.resources.common_search
import com.aspoliakov.securenotes.core_ui.resources.feature_notes_add_note
import com.aspoliakov.securenotes.core_ui.resources.feature_notes_search_notes
import com.aspoliakov.securenotes.core_ui.resources.notes
import com.aspoliakov.securenotes.domain_notes.model.NotesListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

@Composable
fun NotesBrowserScreenRoute(
        modifier: Modifier = Modifier,
        onNavigateToNote: (noteId: String) -> Unit,
        onNavigateToCreateNote: () -> Unit,
) {
    val viewModel = koinMviViewModel<NotesBrowserViewModel>()
    val state by viewModel.state.collectAsState()
    NotesBrowserScreen(
            modifier = modifier,
            state = state,
            effects = viewModel.effects,
            onNavigateToCreateNote = onNavigateToCreateNote,
            onNavigateToNote = onNavigateToNote,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun NotesBrowserScreen(
        modifier: Modifier = Modifier,
        state: NotesBrowserState = NotesBrowserState(),
        effects: Flow<Effect> = emptyFlow(),
        onNavigateToCreateNote: () -> Unit,
        onNavigateToNote: (noteId: String) -> Unit,
        intentHandler: (NotesBrowserIntent) -> Unit = {},
) {
    CollectEffects<NotesBrowserEffect>(effects) { effect ->
        when (effect) {
            is NotesBrowserEffect.ShowSnackbar -> {}
        }
    }
    Box {
        SearchView(
                modifier = modifier
                        .fillMaxWidth()
                        .padding(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp,
                                bottom = 0.dp,
                        ),
                searchState = state.searchState,
                intentHandler = intentHandler,
        )
        Column(
                modifier = modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
        ) {
            when (val notesListState = state.notesListState) {
                is NotesListState.Idle,
                is NotesListState.Loading -> NotesInitView(
                        modifier = modifier,
                )
                is NotesListState.Loaded -> NotesList(
                        modifier = modifier,
                        notesList = notesListState.notesList,
                        onNavigateToNote = onNavigateToNote,
                )
            }
        }
        FloatingButton(
                modifier = modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                onNavigateToCreateNote = onNavigateToCreateNote,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchView(
        modifier: Modifier = Modifier,
        searchState: SearchState = SearchState.Idle,
        intentHandler: (NotesBrowserIntent) -> Unit = {},
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(searchState is SearchState.Searching) }
    val onExpandedChange: (Boolean) -> Unit = {
        expanded = it
    }
    DockedSearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = {
                            query = it
                            intentHandler(NotesBrowserIntent.OnSearch(it))
                        },
                        onSearch = { expanded = false },
                        expanded = expanded,
                        onExpandedChange = onExpandedChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = stringResource(Res.string.feature_notes_search_notes)) },
                        leadingIcon = {
                            if (expanded) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(Res.string.common_back),
                                        modifier = Modifier
                                                .padding(start = 16.dp)
                                                .clickable {
                                                    expanded = false
                                                    query = ""
                                                },
                                )
                            } else {
                                Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = stringResource(Res.string.common_search),
                                        modifier = Modifier.padding(start = 16.dp),
                                )
                            }
                        },
                        trailingIcon = {
                            if (searchState is SearchState.Searching) {
                                CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.secondary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        },
                )
            },
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            modifier = modifier,
            content = {},
    )
}

@Composable
internal fun NotesInitView(
        modifier: Modifier = Modifier,
) {
    Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
internal fun NotesList(
        modifier: Modifier = Modifier,
        notesList: List<NotesListItem>,
        onNavigateToNote: (noteId: String) -> Unit,
) {
    LazyColumn(
            modifier = modifier
                    .padding(horizontal = 16.dp),
    ) {
        items(notesList) {
            NotesListItem(
                    modifier = modifier,
                    onNavigateToNote = onNavigateToNote,
                    notesListItem = it,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun NotesListItem(
        modifier: Modifier = Modifier,
        notesListItem: NotesListItem,
        onNavigateToNote: (noteId: String) -> Unit,
) {
    Card(
            modifier = modifier
                    .padding(
                            vertical = 4.dp,
                    )
                    .clip(CardDefaults.shape)
                    .combinedClickable(
                            onClick = {
                                onNavigateToNote(notesListItem.id)
                            },
                            onLongClick = {
                                //
                            }
                    )
                    .clip(CardDefaults.shape),
            border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
            ),
            colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
        ) {
            Spacer4dp()
            val title = notesListItem.title
            if (title != null) {
                Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                )
                Spacer12dp()
            }
            val body = notesListItem.body
            if (body != null) {
                Text(
                        text = body,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Light,
                        maxLines = 10,
                )
            }
        }
    }
}

@Composable
internal fun FloatingButton(
        modifier: Modifier = Modifier,
        onNavigateToCreateNote: () -> Unit,
) {
    ExtendedFloatingActionButton(
            modifier = modifier,
            onClick = { onNavigateToCreateNote() },
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
