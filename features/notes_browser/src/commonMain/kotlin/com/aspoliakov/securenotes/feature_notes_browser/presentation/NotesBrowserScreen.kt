package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.koinMviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.CollectEffects
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.component.Spacer12dp
import com.aspoliakov.securenotes.core_ui.component.Spacer16dp
import com.aspoliakov.securenotes.core_ui.component.Spacer4dp
import com.aspoliakov.securenotes.core_ui.resources.*
import com.aspoliakov.securenotes.domain_user_state.model.NotesSortOrder
import com.aspoliakov.securenotes.domain_user_state.model.NotesViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.aspoliakov.securenotes.core_ui.Icons as AppIcons

/**
 * Project SecureNotes
 */

private val NoteCardShape = RoundedCornerShape(20.dp)
private val FolderRowShape = RoundedCornerShape(16.dp)
private val SelectionBorderWidth = 3.dp
private val FolderBorderWidth = 1.dp
private const val NOTE_COLOR_TINT_ALPHA = 0.35f
private val DragThreshold = 8.dp
private val AutoScrollEdgeThreshold = 64.dp
private val AutoScrollMaxSpeedPerFrame = 12.dp

@Composable
fun NotesBrowserScreenRoute(
        modifier: Modifier = Modifier,
        onNavigateToNote: (noteId: String) -> Unit,
        onNavigateToCreateNote: (folderId: String?) -> Unit,
        onNavigateToCreateFolder: (parentId: String?) -> Unit,
        onNavigateToEditFolder: (folderId: String) -> Unit,
) {
    val viewModel = koinMviViewModel<NotesBrowserViewModel>()
    val state by viewModel.state.collectAsState()
    NotesBrowserScreen(
            modifier = modifier,
            state = state,
            effects = viewModel.effects,
            onNavigateToNote = onNavigateToNote,
            onNavigateToCreateNote = onNavigateToCreateNote,
            onNavigateToCreateFolder = onNavigateToCreateFolder,
            onNavigateToEditFolder = onNavigateToEditFolder,
            intentHandler = viewModel::emitIntent,
    )
}

@Composable
internal fun NotesBrowserScreen(
        modifier: Modifier = Modifier,
        state: NotesBrowserState = NotesBrowserState(),
        effects: Flow<Effect> = emptyFlow(),
        onNavigateToNote: (noteId: String) -> Unit = {},
        onNavigateToCreateNote: (folderId: String?) -> Unit,
        onNavigateToCreateFolder: (parentId: String?) -> Unit = {},
        onNavigateToEditFolder: (folderId: String) -> Unit = {},
        intentHandler: (NotesBrowserIntent) -> Unit = {},
) {
    CollectEffects<NotesBrowserEffect>(effects) { effect ->
        when (effect) {
            is NotesBrowserEffect.ShowSnackbar -> {}
            is NotesBrowserEffect.NavigateToNote -> onNavigateToNote(effect.noteId)
            is NotesBrowserEffect.NavigateToEditFolder -> onNavigateToEditFolder(effect.folderId)
        }
    }
    val navigationEventState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
    NavigationBackHandler(
            state = navigationEventState,
            isBackEnabled = state.canNavigateBack,
            onBackCompleted = { intentHandler(NotesBrowserIntent.OnNavigateBack) },
    )
    NotesBrowserDialogs(
            state = state,
            intentHandler = intentHandler,
    )
    if (state.isSortSheetVisible) {
        NotesSortBottomSheet(
                sortOrder = state.sortOrder,
                onDismiss = { intentHandler(NotesBrowserIntent.OnSortSheetDismissed) },
                onSortOrderSelected = { intentHandler(NotesBrowserIntent.OnSortOrderSelected(it)) },
        )
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
            NotesBrowserToolbar(
                    state = state,
                    intentHandler = intentHandler,
            )
            if (state.selection is SelectionState.Idle && state.breadcrumb.isNotEmpty()) {
                Spacer12dp()
                FolderPathRow(
                        breadcrumb = state.breadcrumb,
                        onRootClick = { intentHandler(NotesBrowserIntent.OnBreadcrumbClick(null)) },
                        onBreadcrumbClick = { intentHandler(NotesBrowserIntent.OnBreadcrumbClick(it)) },
                )
            }
            Spacer16dp()
            Box(
                    modifier = Modifier.weight(1f),
            ) {
                when (val searchState = state.searchState) {
                    is SearchState.Idle -> BrowserListView(
                            modifier = Modifier.fillMaxSize(),
                            browserListState = state.browserListState,
                            viewMode = state.notesViewMode,
                            selection = state.selection,
                            sortOrder = state.sortOrder,
                            intentHandler = intentHandler,
                    )
                    is SearchState.Active -> BrowserListActiveSearchView(
                            modifier = Modifier.fillMaxSize(),
                            searchState = searchState,
                            selection = state.selection,
                            intentHandler = intentHandler,
                    )
                }
            }
        }
        if (state.selection is SelectionState.Idle) {
            BrowserFabMenu(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp),
                    onCreateFolderClick = { onNavigateToCreateFolder(state.currentFolderId) },
                    onAddNoteClick = { onNavigateToCreateNote(state.currentFolderId) },
            )
        }
    }
}

@Composable
internal fun NotesBrowserToolbar(
        modifier: Modifier = Modifier,
        state: NotesBrowserState,
        intentHandler: (NotesBrowserIntent) -> Unit,
) {
    val selection = state.selection
    if (selection is SelectionState.Active) {
        SelectionTopBar(
                modifier = modifier,
                selectedCount = selection.selectedIds.size,
                availableActions = selection.availableActions,
                onActionClick = { intentHandler(NotesBrowserIntent.OnSelectionActionClick(it)) },
                onExitClick = { intentHandler(NotesBrowserIntent.OnExitSelection) },
        )
    } else {
        Row(
                modifier = modifier.fillMaxWidth(),
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
            Spacer(modifier = Modifier.width(12.dp))
            NotesSortButton(
                    onClick = { intentHandler(NotesBrowserIntent.OnSortButtonClick) },
            )
        }
    }
}

@Composable
private fun NotesBrowserDialogs(
        state: NotesBrowserState,
        intentHandler: (NotesBrowserIntent) -> Unit,
) {
    if (state.pendingBulkDelete) {
        val count = (state.selection as? SelectionState.Active)?.selectedIds?.size ?: 0
        AlertDialog(
                icon = { Icon(imageVector = AppIcons.Delete, contentDescription = null) },
                title = { Text(text = stringResource(Res.string.feature_notes_delete_selected_title)) },
                text = {
                    Text(text = stringResource(Res.string.feature_notes_delete_selected_message, count))
                },
                onDismissRequest = { intentHandler(NotesBrowserIntent.OnDeleteSelectedDismissed) },
                confirmButton = {
                    TextButton(onClick = { intentHandler(NotesBrowserIntent.OnDeleteSelectedConfirmed) }) {
                        Text(text = stringResource(Res.string.common_confirm), fontWeight = FontWeight.Medium)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { intentHandler(NotesBrowserIntent.OnDeleteSelectedDismissed) }) {
                        Text(text = stringResource(Res.string.common_cancel))
                    }
                },
        )
    }
}

@Composable
internal fun FolderPathRow(
        modifier: Modifier = Modifier,
        breadcrumb: List<FolderBreadcrumbItem>,
        onRootClick: () -> Unit,
        onBreadcrumbClick: (String?) -> Unit,
) {
    Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
                onClick = onRootClick,
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Text(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    text = stringResource(Res.string.feature_notes_root_folder),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
        Text(
                modifier = Modifier.padding(start = 4.dp),
                text = "/",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
        )
        breadcrumb.forEach { item ->
            Text(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .clickable { onBreadcrumbClick(item.id) },
                    text = "${item.name}/",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
            )
        }
    }
}

@Composable
internal fun SelectionTopBar(
        modifier: Modifier = Modifier,
        selectedCount: Int,
        availableActions: Set<SelectionAction>,
        onActionClick: (SelectionAction) -> Unit,
        onExitClick: () -> Unit,
) {
    Row(
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onExitClick) {
            Icon(
                    imageVector = AppIcons.Close,
                    contentDescription = stringResource(Res.string.feature_notes_exit_selection),
            )
        }
        Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.feature_notes_selected_count, selectedCount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
        )
        SelectionAction.entries.forEach { action ->
            if (action in availableActions) {
                SelectionActionButton(
                        action = action,
                        onClick = { onActionClick(action) },
                )
            }
        }
    }
}

@Composable
private fun SelectionActionButton(
        action: SelectionAction,
        onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        when (action) {
            SelectionAction.RENAME -> Icon(
                    imageVector = AppIcons.Rename,
                    contentDescription = stringResource(Res.string.common_rename),
            )
            SelectionAction.DELETE -> Icon(
                    imageVector = AppIcons.Delete,
                    contentDescription = stringResource(Res.string.common_delete),
                    tint = MaterialTheme.colorScheme.error,
            )
        }
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
internal fun NotesSortButton(
        modifier: Modifier = Modifier,
        onClick: () -> Unit = {},
) {
    Box(
            modifier = modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .clickable { onClick() },
            contentAlignment = Alignment.Center,
    ) {
        Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = stringResource(Res.string.feature_notes_sort_title),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotesSortBottomSheet(
        sortOrder: NotesSortOrder,
        onDismiss: () -> Unit = {},
        onSortOrderSelected: (NotesSortOrder) -> Unit = {},
) {
    ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
        ) {
            Text(
                    text = stringResource(Res.string.feature_notes_sort_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer16dp()
            SortOrderOptionRow(
                    label = stringResource(Res.string.feature_notes_sort_newest_first),
                    selected = sortOrder == NotesSortOrder.NEWEST_FIRST,
                    onClick = { onSortOrderSelected(NotesSortOrder.NEWEST_FIRST) },
            )
            SortOrderOptionRow(
                    label = stringResource(Res.string.feature_notes_sort_oldest_first),
                    selected = sortOrder == NotesSortOrder.OLDEST_FIRST,
                    onClick = { onSortOrderSelected(NotesSortOrder.OLDEST_FIRST) },
            )
            SortOrderOptionRow(
                    label = stringResource(Res.string.feature_notes_sort_custom),
                    selected = sortOrder == NotesSortOrder.CUSTOM,
                    onClick = { onSortOrderSelected(NotesSortOrder.CUSTOM) },
            )
        }
    }
}

@Composable
private fun SortOrderOptionRow(
        label: String,
        selected: Boolean,
        onClick: () -> Unit,
) {
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
internal fun BrowserListView(
        modifier: Modifier = Modifier,
        browserListState: BrowserListState,
        viewMode: NotesViewMode,
        selection: SelectionState,
        sortOrder: NotesSortOrder,
        intentHandler: (NotesBrowserIntent) -> Unit,
) {
    when (browserListState) {
        is BrowserListState.Idle -> NotesListLoadingView(modifier = modifier)
        is BrowserListState.Loaded -> if (browserListState.items.isNotEmpty()) {
            BrowserListContentView(
                    modifier = modifier,
                    items = browserListState.items,
                    viewMode = viewMode,
                    selection = selection,
                    reorderEnabled = sortOrder == NotesSortOrder.CUSTOM,
                    intentHandler = intentHandler,
            )
        } else {
            NotesListPlaceholderView(
                    modifier = modifier,
                    title = stringResource(Res.string.feature_notes_empty_title),
                    subtitle = stringResource(Res.string.feature_notes_empty_subtitle),
                    icon = Res.drawable.ic_notes,
            )
        }
    }
}

@Composable
internal fun BrowserListActiveSearchView(
        modifier: Modifier = Modifier,
        searchState: SearchState.Active,
        selection: SelectionState,
        intentHandler: (NotesBrowserIntent) -> Unit,
) {
    when {
        searchState.results.isNotEmpty() -> BrowserFlatListView(
                modifier = modifier,
                items = searchState.results,
                selection = selection,
                intentHandler = intentHandler,
        )
        searchState.inProgress -> NotesListLoadingView(modifier = modifier)
        else -> NotesListPlaceholderView(
                modifier = modifier,
                title = stringResource(Res.string.feature_notes_no_results_title),
                subtitle = stringResource(Res.string.feature_notes_no_results_subtitle),
                icon = Res.drawable.ic_search,
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
internal fun BrowserListContentView(
        modifier: Modifier = Modifier,
        items: List<BrowserListItem>,
        viewMode: NotesViewMode,
        selection: SelectionState,
        reorderEnabled: Boolean = false,
        intentHandler: (NotesBrowserIntent) -> Unit,
) {
    when (viewMode) {
        NotesViewMode.LIST -> BrowserFlatListView(
                modifier = modifier,
                items = items,
                selection = selection,
                reorderEnabled = reorderEnabled,
                intentHandler = intentHandler,
        )
        NotesViewMode.GRID -> BrowserGridView(
                modifier = modifier,
                items = items,
                selection = selection,
                reorderEnabled = reorderEnabled,
                intentHandler = intentHandler,
        )
    }
}

@Composable
internal fun BrowserFlatListView(
        modifier: Modifier = Modifier,
        items: List<BrowserListItem>,
        selection: SelectionState,
        reorderEnabled: Boolean = false,
        intentHandler: (NotesBrowserIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    val dragState = remember { DragReorderState<BrowserListItem> { it.id } }
    val renderedItems = if (dragState.draggingKey != null) dragState.displayedItems else items
    val density = LocalDensity.current
    LaunchedEffect(dragState.draggingKey) {
        val draggingKey = dragState.draggingKey ?: return@LaunchedEffect
        runDragAutoScroll(
                dragState = dragState,
                draggingKey = draggingKey,
                density = density,
                edgeThreshold = AutoScrollEdgeThreshold,
                maxSpeedPerFrame = AutoScrollMaxSpeedPerFrame,
                viewportStart = { listState.layoutInfo.viewportStartOffset.toFloat() },
                viewportEnd = { listState.layoutInfo.viewportEndOffset.toFloat() },
                draggedItemExtent = {
                    listState.layoutInfo.visibleItemsInfo.find { it.key == draggingKey }?.size?.toFloat()
                },
                scrollBy = { amount -> listState.scrollBy(amount) },
                recomputeTargetIndex = { findTargetIndexInList(listState, dragState) },
        )
    }
    LazyColumn(
            state = listState,
            modifier = modifier,
            userScrollEnabled = dragState.draggingKey == null,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        items(renderedItems, key = { it.id }) { item ->
            val itemPlacementModifier = if (dragState.isDragging(item)) Modifier else Modifier.animateItem()
            BrowserListItemContent(
                    modifier = itemPlacementModifier,
                    item = item,
                    selection = selection,
                    reorderEnabled = reorderEnabled,
                    dragState = dragState,
                    items = items,
                    findTargetIndex = { findTargetIndexInList(listState, dragState) },
                    naturalOffset = { naturalOffsetInList(listState, item.id) },
                    intentHandler = intentHandler,
            )
        }
    }
}

@Composable
internal fun BrowserGridView(
        modifier: Modifier = Modifier,
        items: List<BrowserListItem>,
        selection: SelectionState,
        reorderEnabled: Boolean = false,
        intentHandler: (NotesBrowserIntent) -> Unit,
) {
    val gridState = rememberLazyStaggeredGridState()
    val dragState = remember { DragReorderState<BrowserListItem> { it.id } }
    val renderedItems = if (dragState.draggingKey != null) dragState.displayedItems else items
    val density = LocalDensity.current
    LaunchedEffect(dragState.draggingKey) {
        val draggingKey = dragState.draggingKey ?: return@LaunchedEffect
        runDragAutoScroll(
                dragState = dragState,
                draggingKey = draggingKey,
                density = density,
                edgeThreshold = AutoScrollEdgeThreshold,
                maxSpeedPerFrame = AutoScrollMaxSpeedPerFrame,
                viewportStart = { gridState.layoutInfo.viewportStartOffset.toFloat() },
                viewportEnd = { gridState.layoutInfo.viewportEndOffset.toFloat() },
                draggedItemExtent = {
                    gridState.layoutInfo.visibleItemsInfo.find { it.key == draggingKey }?.size?.height?.toFloat()
                },
                scrollBy = { amount -> gridState.scrollBy(amount) },
                recomputeTargetIndex = { findTargetIndexInGrid(gridState, dragState) },
        )
    }
    LazyVerticalStaggeredGrid(
            state = gridState,
            modifier = modifier,
            userScrollEnabled = dragState.draggingKey == null,
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
    ) {
        items(renderedItems, key = { it.id }) { item ->
            val itemPlacementModifier = if (dragState.isDragging(item)) Modifier else Modifier.animateItem()
            BrowserListItemContent(
                    modifier = itemPlacementModifier,
                    item = item,
                    selection = selection,
                    reorderEnabled = reorderEnabled,
                    dragState = dragState,
                    items = items,
                    findTargetIndex = { findTargetIndexInGrid(gridState, dragState) },
                    naturalOffset = { naturalOffsetInGrid(gridState, item.id) },
                    intentHandler = intentHandler,
            )
        }
    }
}

@Composable
private fun BrowserListItemContent(
        modifier: Modifier = Modifier,
        item: BrowserListItem,
        selection: SelectionState,
        reorderEnabled: Boolean,
        dragState: DragReorderState<BrowserListItem>,
        items: List<BrowserListItem>,
        findTargetIndex: () -> Int?,
        naturalOffset: () -> Offset?,
        intentHandler: (NotesBrowserIntent) -> Unit,
) {
    val itemShape = when (item) {
        is BrowserListItem.NotesBrowserFolderItem -> FolderRowShape
        is BrowserListItem.NotesBrowserNoteItem -> NoteCardShape
    }
    val interactionSource = remember { MutableInteractionSource() }
    val itemModifier = modifier.then(
            if (reorderEnabled) {
                dragReorderModifier(
                        item = item,
                        dragState = dragState,
                        items = items,
                        shape = itemShape,
                        dragThreshold = DragThreshold,
                        interactionSource = interactionSource,
                        findTargetIndex = findTargetIndex,
                        naturalOffset = naturalOffset,
                        canEnterDrag = { selection is SelectionState.Idle },
                        onClick = { intentHandler(NotesBrowserIntent.OnItemClick(item.id)) },
                        onLongPress = { intentHandler(NotesBrowserIntent.OnItemLongClick(item.id)) },
                        onExitSelection = { intentHandler(NotesBrowserIntent.OnExitSelection) },
                        onReordered = { reorderedItem, targetIndex ->
                            intentHandler(NotesBrowserIntent.OnItemReordered(reorderedItem.id, targetIndex))
                        },
                )
            } else {
                Modifier
            }
    )
    val onClick: (() -> Unit)? = if (reorderEnabled) {
        null
    } else {
        { intentHandler(NotesBrowserIntent.OnItemClick(item.id)) }
    }
    val onLongClick: (() -> Unit)? = if (reorderEnabled) {
        null
    } else {
        { intentHandler(NotesBrowserIntent.OnItemLongClick(item.id)) }
    }
    val rippleInteractionSource = if (reorderEnabled) interactionSource else null
    when (item) {
        is BrowserListItem.NotesBrowserFolderItem -> FolderListItemView(
                modifier = itemModifier,
                folder = item,
                selection = selection,
                isDragging = dragState.isDragging(item),
                interactionSource = rippleInteractionSource,
                onClick = onClick,
                onLongClick = onLongClick,
        )
        is BrowserListItem.NotesBrowserNoteItem -> NoteListItemView(
                modifier = itemModifier,
                note = item,
                selection = selection,
                interactionSource = rippleInteractionSource,
                onClick = onClick,
                onLongClick = onLongClick,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FolderListItemView(
        modifier: Modifier = Modifier,
        folder: BrowserListItem.NotesBrowserFolderItem,
        selection: SelectionState,
        isDragging: Boolean = false,
        interactionSource: MutableInteractionSource? = null,
        onClick: (() -> Unit)?,
        onLongClick: (() -> Unit)?,
) {
    val isSelected = selection is SelectionState.Active && selection.selectedIds.contains(folder.id)
    val borderWidth = if (isSelected) SelectionBorderWidth else FolderBorderWidth
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val dragBackgroundColor = if (isDragging) MaterialTheme.colorScheme.surfaceContainerLow else Color.Transparent
    Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(FolderRowShape)
                .background(dragBackgroundColor)
                .border(width = borderWidth, color = borderColor, shape = FolderRowShape)
                .then(
                        if (onClick != null) {
                            Modifier.combinedClickable(
                                    onClick = onClick,
                                    onLongClick = onLongClick,
                            )
                        } else if (interactionSource != null) {
                            Modifier.indication(interactionSource, LocalIndication.current)
                        } else {
                            Modifier
                        }
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
                imageVector = AppIcons.Folder,
                contentDescription = stringResource(Res.string.feature_notes_folder_icon_description),
                tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
                modifier = Modifier.weight(1f),
                text = folder.name.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun NoteListItemView(
        modifier: Modifier = Modifier,
        note: BrowserListItem.NotesBrowserNoteItem,
        selection: SelectionState = SelectionState.Idle,
        interactionSource: MutableInteractionSource? = null,
        onClick: (() -> Unit)? = {},
        onLongClick: (() -> Unit)? = {},
) {
    val isSelected = selection is SelectionState.Active && selection.selectedIds.contains(note.id)
    val noteColor = note.color
    val containerColor = if (noteColor == null) {
        MaterialTheme.colorScheme.surfaceContainerLow
    } else {
        Color(noteColor)
            .copy(alpha = NOTE_COLOR_TINT_ALPHA)
            .compositeOver(MaterialTheme.colorScheme.surfaceContainerLow)
    }
    val cardModifier = modifier
        .fillMaxWidth()
        .clip(NoteCardShape)
        .background(containerColor)
        .then(
                if (isSelected) {
                    Modifier.border(
                            width = SelectionBorderWidth,
                            color = MaterialTheme.colorScheme.primary,
                            shape = NoteCardShape,
                    )
                } else {
                    Modifier
                }
        )
    Column(
            modifier = cardModifier
                .then(
                        if (onClick != null) {
                            Modifier.combinedClickable(
                                    onClick = onClick,
                                    onLongClick = onLongClick,
                            )
                        } else if (interactionSource != null) {
                            Modifier.indication(interactionSource, LocalIndication.current)
                        } else {
                            Modifier
                        }
                )
                .padding(16.dp),
    ) {
        val title = note.title
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
        val body = note.body
        if (!body.isNullOrBlank()) {
            Spacer12dp()
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
internal fun BrowserFabMenu(
        modifier: Modifier = Modifier,
        onCreateFolderClick: () -> Unit,
        onAddNoteClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
            modifier = modifier,
            horizontalAlignment = Alignment.End,
    ) {
        AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
        ) {
            Column(horizontalAlignment = Alignment.End) {
                FabMenuAction(
                        label = stringResource(Res.string.feature_notes_new_folder),
                        icon = {
                            Icon(
                                    imageVector = AppIcons.CreateFolder,
                                    contentDescription = null,
                            )
                        },
                        onClick = {
                            expanded = false
                            onCreateFolderClick()
                        },
                )
                Spacer12dp()
                FabMenuAction(
                        label = stringResource(Res.string.feature_notes_add_note),
                        icon = {
                            Icon(
                                    painter = painterResource(Res.drawable.ic_notes),
                                    contentDescription = null,
                            )
                        },
                        onClick = {
                            expanded = false
                            onAddNoteClick()
                        },
                )
                Spacer12dp()
            }
        }
        FloatingActionButton(onClick = { expanded = !expanded }) {
            Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (expanded) {
                        stringResource(Res.string.feature_notes_add_menu_close)
                    } else {
                        stringResource(Res.string.feature_notes_add_menu_open)
                    },
            )
        }
    }
}

@Composable
private fun FabMenuAction(
        label: String,
        icon: @Composable () -> Unit,
        onClick: () -> Unit,
) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Text(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
            )
        }
        SmallFloatingActionButton(
                onClick = onClick,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ) {
            icon()
        }
    }
}

@Preview
@Composable
private fun NotesBrowserScreenLoadingPreview() {
    AppTheme {
        NotesBrowserScreen(
                state = NotesBrowserState(),
                onNavigateToCreateNote = {},
        )
    }
}

@Preview
@Composable
private fun NotesBrowserScreenEmptyPreview() {
    AppTheme {
        NotesBrowserScreen(
                state = NotesBrowserState(
                        browserListState = BrowserListState.Loaded(
                                items = emptyList(),
                        ),
                ),
                onNavigateToCreateNote = {},
        )
    }
}

@Preview
@Composable
private fun NotesBrowserScreenListPreview() {
    AppTheme {
        NotesBrowserScreen(
                state = NotesBrowserState(
                        breadcrumb = listOf(
                                FolderBreadcrumbItem(
                                        id = "1",
                                        name = "ideas",
                                )
                        ),
                        currentFolderId = "1",
                        canNavigateBack = true,
                        browserListState = BrowserListState.Loaded(
                                items = listOf(
                                        BrowserListItem.NotesBrowserFolderItem(
                                                id = "f1",
                                                parentId = "1",
                                                createdAt = 0L,
                                                order = 0.0,
                                                name = "Subfolder",
                                        ),
                                        BrowserListItem.NotesBrowserNoteItem(
                                                id = "1",
                                                createdAt = 0L,
                                                order = 1000.0,
                                                title = "Title 1",
                                                body = "Body 1",
                                                color = null,
                                                folderId = "1",
                                        ),
                                        BrowserListItem.NotesBrowserNoteItem(
                                                id = "2",
                                                createdAt = 0L,
                                                order = 2000.0,
                                                title = "Title 2",
                                                body = "Body 2 with more text to show card wrapping.",
                                                color = 0xFFE91E63L,
                                                folderId = "1",
                                        ),
                                ),
                        ),
                        notesViewMode = NotesViewMode.GRID,
                ),
                onNavigateToCreateNote = {},
        )
    }
}

@Preview
@Composable
private fun NotesBrowserScreenSearchPreview() {
    AppTheme {
        NotesBrowserScreen(
                state = NotesBrowserState(
                        browserListState = BrowserListState.Loaded(
                                items = emptyList(),
                        ),
                        searchState = SearchState.Active(
                                query = "Title 2",
                                results = emptyList(),
                                inProgress = true,
                        )
                ),
                onNavigateToCreateNote = {},
        )
    }
}
