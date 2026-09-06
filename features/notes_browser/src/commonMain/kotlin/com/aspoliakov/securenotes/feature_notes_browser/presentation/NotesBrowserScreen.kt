package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.component.Spacer12dp
import com.aspoliakov.securenotes.core_ui.component.Spacer16dp
import com.aspoliakov.securenotes.core_ui.component.Spacer4dp
import com.aspoliakov.securenotes.core_ui.resources.*
import com.aspoliakov.securenotes.domain_notes.model.NotesListItem
import com.aspoliakov.securenotes.domain_user_state.model.NotesViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 */

private val NoteCardShape = RoundedCornerShape(20.dp)

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
    Box(
            modifier = modifier.fillMaxSize(),
    ) {
        Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
            ) {
                NotesSearchView(
                        modifier = Modifier.weight(1f),
                        searchState = state.searchState,
                        intentHandler = intentHandler,
                )
                Spacer(modifier = Modifier.width(12.dp))
                NotesViewModeToggleButton(
                        viewMode = state.notesViewMode,
                        onToggle = { intentHandler(NotesBrowserIntent.OnToggleViewMode) },
                )
            }
            Spacer16dp()
            Box(
                    modifier = Modifier.weight(1f),
            ) {
                when (val searchState = state.searchState) {
                    is SearchState.Idle -> NotesListView(
                            modifier = Modifier.fillMaxSize(),
                            notesListState = state.notesListState,
                            viewMode = state.notesViewMode,
                            onNavigateToNote = onNavigateToNote,
                    )
                    is SearchState.Active -> NotesListActiveSearchView(
                            modifier = Modifier.fillMaxSize(),
                            searchState = searchState,
                            viewMode = state.notesViewMode,
                            onNavigateToNote = onNavigateToNote,
                    )
                }
            }
        }
        AddNoteButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                onNavigateToCreateNote = onNavigateToCreateNote,
        )
    }
}

@Composable
internal fun NotesSearchView(
        modifier: Modifier = Modifier,
        searchState: SearchState = SearchState.Idle,
        intentHandler: (NotesBrowserIntent) -> Unit = {},
) {
    val activeSearch = searchState as? SearchState.Active
    var query by remember { mutableStateOf(activeSearch?.query.orEmpty()) }
    LaunchedEffect(searchState) {
        if (searchState is SearchState.Idle && query.isNotEmpty()) {
            query = ""
        }
    }
    TextField(
            value = query,
            onValueChange = {
                query = it
                intentHandler(NotesBrowserIntent.OnSearch(it))
            },
            modifier = modifier.clip(RoundedCornerShape(16.dp)),
            singleLine = true,
            placeholder = { Text(text = stringResource(Res.string.feature_notes_search_notes)) },
            leadingIcon = {
                Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(Res.string.common_search),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingIcon = {
                if (activeSearch != null) {
                    Row(
                            modifier = Modifier.padding(end = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                                modifier = Modifier.size(20.dp),
                                contentAlignment = Alignment.Center,
                        ) {
                            if (activeSearch.inProgress) {
                                CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.secondary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                            }
                        }
                        Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(Res.string.common_cancel),
                                modifier = Modifier.clickable {
                                    query = ""
                                    intentHandler(NotesBrowserIntent.OnSearch(""))
                                },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
            ),
    )
}

@Composable
internal fun NotesViewModeToggleButton(
        modifier: Modifier = Modifier,
        viewMode: NotesViewMode = NotesViewMode.LIST,
        onToggle: () -> Unit = {},
) {
    val icon = when (viewMode) {
        NotesViewMode.LIST -> Icons.Default.GridView
        NotesViewMode.GRID -> Icons.AutoMirrored.Filled.ViewList
    }
    val contentDescription = when (viewMode) {
        NotesViewMode.LIST -> stringResource(Res.string.feature_notes_view_grid)
        NotesViewMode.GRID -> stringResource(Res.string.feature_notes_view_list)
    }
    Box(
            modifier = modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .clickable { onToggle() },
            contentAlignment = Alignment.Center,
    ) {
        Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun NotesListView(
        modifier: Modifier = Modifier,
        notesListState: NotesListState,
        viewMode: NotesViewMode,
        onNavigateToNote: (noteId: String) -> Unit,
) {
    when (notesListState) {
        is NotesListState.Idle,
        is NotesListState.Loading -> NotesListLoadingView(
                modifier = modifier,
        )
        is NotesListState.Loaded -> if (notesListState.notesList.isNotEmpty()) {
            NotesListContentView(
                    modifier = modifier,
                    viewMode = viewMode,
                    notesList = notesListState.notesList,
                    onNavigateToNote = onNavigateToNote,
            )
        } else {
            NotesListPlaceholderView(
                    modifier = modifier,
                    title = stringResource(Res.string.feature_notes_empty_title),
                    subtitle = stringResource(Res.string.feature_notes_empty_subtitle),
                    icon = Res.drawable.notes,
            )
        }
    }
}

@Composable
internal fun NotesListActiveSearchView(
        modifier: Modifier = Modifier,
        searchState: SearchState.Active,
        viewMode: NotesViewMode,
        onNavigateToNote: (noteId: String) -> Unit,
) {
    when {
        searchState.results.isNotEmpty() -> NotesListContentView(
                modifier = modifier,
                viewMode = viewMode,
                notesList = searchState.results,
                onNavigateToNote = onNavigateToNote,
        )
        searchState.inProgress -> NotesListLoadingView(
                modifier = modifier,
        )
        else -> NotesListPlaceholderView(
                modifier = modifier,
                title = stringResource(Res.string.feature_notes_no_results_title),
                subtitle = stringResource(Res.string.feature_notes_no_results_subtitle),
                icon = Res.drawable.search,
        )
    }
}

@Composable
internal fun NotesListLoadingView(
        modifier: Modifier = Modifier,
) {
    Column(
            modifier = modifier,
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
internal fun NotesListContentView(
        modifier: Modifier = Modifier,
        viewMode: NotesViewMode,
        notesList: List<NotesListItem>,
        onNavigateToNote: (noteId: String) -> Unit,
) {
    when (viewMode) {
        NotesViewMode.LIST -> NotesListContentListView(
                modifier = modifier,
                notesList = notesList,
                onNavigateToNote = onNavigateToNote,
        )
        NotesViewMode.GRID -> NotesListContentGridView(
                modifier = modifier,
                notesList = notesList,
                onNavigateToNote = onNavigateToNote,
        )
    }
}

@Composable
private fun NotesListPlaceholderView(
        modifier: Modifier = Modifier,
        title: String,
        subtitle: String,
        icon: DrawableResource,
) {
    Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
    ) {
        Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
                contentAlignment = Alignment.Center,
        ) {
            Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer16dp()
        Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer4dp()
        Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}

@Composable
internal fun NotesListContentListView(
        modifier: Modifier = Modifier,
        notesList: List<NotesListItem>,
        onNavigateToNote: (noteId: String) -> Unit,
) {
    LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        items(notesList) { item ->
            NoteListItemView(
                    onNavigateToNote = onNavigateToNote,
                    notesListItem = item,
            )
        }
    }
}

@Composable
internal fun NotesListContentGridView(
        modifier: Modifier = Modifier,
        notesList: List<NotesListItem>,
        onNavigateToNote: (noteId: String) -> Unit,
) {
    LazyVerticalStaggeredGrid(
            modifier = modifier,
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        items(notesList) { item ->
            NoteListItemView(
                    onNavigateToNote = onNavigateToNote,
                    notesListItem = item,
            )
        }
    }
}

@Composable
internal fun NoteListItemView(
        modifier: Modifier = Modifier,
        notesListItem: NotesListItem,
        onNavigateToNote: (noteId: String) -> Unit,
) {
    val color = notesListItem.color
    val cardModifier = modifier
        .fillMaxWidth()
        .clip(NoteCardShape)
        .background(MaterialTheme.colorScheme.surfaceContainerLow)
        .then(
                if (color != null) {
                    Modifier.border(
                            width = 2.dp,
                            color = Color(color),
                            shape = NoteCardShape,
                    )
                } else {
                    Modifier
                }
        )
    Column(
            modifier = cardModifier
                .clickable { onNavigateToNote(notesListItem.id) }
                .padding(16.dp),
    ) {
        val title = notesListItem.title
        if (!title.isNullOrBlank()) {
            Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
            )
        }
        val body = notesListItem.body
        if (!body.isNullOrBlank()) {
            if (!title.isNullOrBlank()) {
                Spacer12dp()
            }
            Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun AddNoteButton(
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

@Preview
@Composable
private fun NotesBrowserScreenLoadingPreview() {
    AppTheme {
        NotesBrowserScreen(
                state = NotesBrowserState(),
                onNavigateToCreateNote = {},
                onNavigateToNote = {},
        )
    }
}

@Preview
@Composable
private fun NotesBrowserScreenEmptyPreview() {
    AppTheme {
        NotesBrowserScreen(
                state = NotesBrowserState(
                        notesListState = NotesListState.Loaded(
                                notesList = emptyList(),
                        ),
                ),
                onNavigateToCreateNote = {},
                onNavigateToNote = {},
        )
    }
}

@Preview
@Composable
private fun NotesBrowserScreenListPreview() {
    AppTheme {
        NotesBrowserScreen(
                state = NotesBrowserState(
                        notesListState = NotesListState.Loaded(
                                notesList = listOf(
                                        NotesListItem(
                                                id = "1",
                                                createdAt = 0L,
                                                title = "Title 1",
                                                body = "Body 1",
                                        ),
                                        NotesListItem(
                                                id = "2",
                                                createdAt = 0L,
                                                title = "Title 2",
                                                body = "Body 2 with more text to show card wrapping.",
                                                color = 0xFFE91E63L,
                                        ),
                                        NotesListItem(
                                                id = "3",
                                                createdAt = 0L,
                                                title = null,
                                                body = "Body 3",
                                        ),
                                ),
                        ),
                        notesViewMode = NotesViewMode.GRID,
                ),
                onNavigateToCreateNote = {},
                onNavigateToNote = {},
        )
    }
}

@Preview
@Composable
private fun NotesBrowserScreenSearchPreview() {
    AppTheme {
        NotesBrowserScreen(
                state = NotesBrowserState(
                        notesListState = NotesListState.Loaded(
                                notesList = emptyList(),
                        ),
                        searchState = SearchState.Active(
                                query = "Title 2",
                                results = emptyList(),
                                inProgress = true,
                        )
                ),
                onNavigateToCreateNote = {},
                onNavigateToNote = {},
        )
    }
}
