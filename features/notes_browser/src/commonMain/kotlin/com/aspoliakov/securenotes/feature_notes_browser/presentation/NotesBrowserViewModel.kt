package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_base.util.flowOnIO
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_folders.FolderInteractor
import com.aspoliakov.securenotes.domain_folders.FoldersListInteractor
import com.aspoliakov.securenotes.domain_folders.model.FolderVO
import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import com.aspoliakov.securenotes.domain_notes.model.NoteVO
import com.aspoliakov.securenotes.domain_user_state.UserPrefsInteractor
import com.aspoliakov.securenotes.domain_user_state.model.NotesSortOrder
import com.aspoliakov.securenotes.domain_user_state.model.NotesViewMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Project SecureNotes
 */

internal class NotesBrowserViewModel(
        initialState: NotesBrowserState,
        private val notesListInteractor: NotesListInteractor,
        private val folderInteractor: FolderInteractor,
        private val foldersListInteractor: FoldersListInteractor,
        private val noteInteractor: NoteInteractor,
        private val userPrefsInteractor: UserPrefsInteractor,
) : MviViewModel<NotesBrowserState, NotesBrowserEffect, NotesBrowserIntent>(initialState) {

    private companion object {
        private const val ORDER_STEP = 1000.0
    }

    private var browseJob: Job? = null
    private var searchJob: Job? = null

    private val reorderJobs: MutableMap<String, Job> = mutableMapOf()

    private val backStack = ArrayDeque<String?>()

    init {
        loadCurrentFolder()
    }

    override fun handleIntent(intent: NotesBrowserIntent) {
        when (intent) {
            is NotesBrowserIntent.OnSearch -> onSearch(intent.query)
            is NotesBrowserIntent.OnToggleViewMode -> onToggleViewMode()
            is NotesBrowserIntent.OnSortButtonClick -> onSortButtonClick()
            is NotesBrowserIntent.OnSortOrderSelected -> onSortOrderSelected(intent.sortOrder)
            is NotesBrowserIntent.OnSortSheetDismissed -> onSortSheetDismissed()
            is NotesBrowserIntent.OnItemReordered -> onItemReordered(intent.itemId, intent.targetIndex)
            is NotesBrowserIntent.OnItemClick -> onItemClick(intent.itemId)
            is NotesBrowserIntent.OnItemLongClick -> onItemLongClick(intent.itemId)
            is NotesBrowserIntent.OnBreadcrumbClick -> navigateToFolder(intent.folderId)
            is NotesBrowserIntent.OnNavigateBack -> onNavigateBack()
            is NotesBrowserIntent.OnExitSelection -> onExitSelection()
            is NotesBrowserIntent.OnSelectionActionClick -> onSelectionActionClick(intent.action)
            is NotesBrowserIntent.OnDeleteSelectedDismissed -> onDeleteSelectedDismissed()
            is NotesBrowserIntent.OnDeleteSelectedConfirmed -> onDeleteSelectedConfirmed()
        }
    }

    private fun onSearch(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            reduceState { copy(searchState = SearchState.Idle) }
            return
        }
        searchJob = launchOnIO {
            val previousResults = (currentState.searchState as? SearchState.Active)?.results ?: emptyList()
            reduceState {
                copy(
                        searchState = SearchState.Active(
                                query = query,
                                results = previousResults,
                                inProgress = true,
                        )
                )
            }
            val foundFolders = foldersListInteractor.searchFolders(query)
            val foundNotes = notesListInteractor.searchNotes(query)
            val foundFoldersBrowserItems: List<BrowserListItem> = foundFolders.map { it.toBrowserItem() }
            val foundNotesBrowserItems: List<BrowserListItem> = foundNotes.map { it.toBrowserItem() }
            val sortedResults = (foundFoldersBrowserItems + foundNotesBrowserItems)
                .sortedByDescending { it.createdAt }
            reduceState {
                copy(
                        searchState = SearchState.Active(
                                query = query,
                                results = sortedResults,
                                inProgress = false,
                        )
                )
            }
        }
    }

    private fun onToggleViewMode() {
        val nextViewMode = when (currentState.notesViewMode) {
            NotesViewMode.LIST -> NotesViewMode.GRID
            NotesViewMode.GRID -> NotesViewMode.LIST
        }
        reduceState { copy(notesViewMode = nextViewMode) }
        launchOnIO {
            userPrefsInteractor.setNotesViewMode(nextViewMode)
        }
    }

    private fun onSortButtonClick() {
        reduceState { copy(isSortSheetVisible = true) }
    }

    private fun onSortOrderSelected(sortOrder: NotesSortOrder) {
        reduceState {
            copy(
                    sortOrder = sortOrder,
                    isSortSheetVisible = false,
            )
        }
        launchOnIO { userPrefsInteractor.setNotesSortOrder(sortOrder) }
        loadCurrentFolder()
    }

    private fun onSortSheetDismissed() {
        reduceState { copy(isSortSheetVisible = false) }
    }

    private fun onItemReordered(itemId: String, targetIndex: Int) {
        val browserListState = currentState.browserListState as? BrowserListState.Loaded
        if (browserListState == null || currentState.sortOrder != NotesSortOrder.CUSTOM) return
        val items = browserListState.items.toMutableList()
        val currentIndex = items.indexOfFirst { it.id == itemId }
        if (currentIndex == -1) return
        val draggedItem = items.removeAt(currentIndex)
        val insertIndex = targetIndex.coerceIn(0, items.size)
        val previousOrder = items.getOrNull(insertIndex - 1)?.order
        val nextOrder = items.getOrNull(insertIndex)?.order
        val newOrder = if (previousOrder != null && nextOrder != null) {
            (previousOrder + nextOrder) / 2.0
        } else if (previousOrder != null) {
            previousOrder + ORDER_STEP
        } else if (nextOrder != null) {
            nextOrder - ORDER_STEP
        } else {
            ORDER_STEP
        }
        val reorderedItem: BrowserListItem = when (draggedItem) {
            is BrowserListItem.NotesBrowserFolderItem -> draggedItem.copy(order = newOrder)
            is BrowserListItem.NotesBrowserNoteItem -> draggedItem.copy(order = newOrder)
        }
        items.add(insertIndex, reorderedItem)
        reduceState { copy(browserListState = BrowserListState.Loaded(items)) }
        reorderJobs[draggedItem.id]?.cancel()
        reorderJobs[draggedItem.id] = launchOnIO {
            when (draggedItem) {
                is BrowserListItem.NotesBrowserFolderItem -> folderInteractor.reorder(draggedItem.id, newOrder)
                is BrowserListItem.NotesBrowserNoteItem -> noteInteractor.reorder(draggedItem.id, newOrder)
            }
        }
    }

    private fun onItemClick(itemId: String) {
        if (currentState.selection is SelectionState.Active) {
            toggleSelect(itemId)
            return
        }
        when (val item = currentVisibleItems().find { it.id == itemId }) {
            is BrowserListItem.NotesBrowserFolderItem -> navigateToFolder(item.id)
            is BrowserListItem.NotesBrowserNoteItem -> sendEffect { NotesBrowserEffect.NavigateToNote(item.id) }
            null -> Unit
        }
    }

    private fun onItemLongClick(itemId: String) {
        val selection = currentState.selection
        if (selection is SelectionState.Active) {
            toggleSelect(itemId)
        } else {
            val ids = setOf(itemId)
            reduceState {
                copy(
                        selection = SelectionState.Active(
                                selectedIds = ids,
                                availableActions = availableActions(ids),
                        )
                )
            }
        }
    }

    private fun navigateToFolder(folderId: String?) {
        if (folderId == currentState.currentFolderId) return
        backStack.addLast(currentState.currentFolderId)
        moveToFolder(folderId)
    }

    private fun onNavigateBack() {
        if (backStack.isEmpty()) return
        val previousFolderId = backStack.removeLast()
        moveToFolder(previousFolderId)
    }

    private fun onExitSelection() {
        reduceState { copy(selection = SelectionState.Idle) }
    }

    private fun onSelectionActionClick(action: SelectionAction) {
        when (action) {
            SelectionAction.RENAME -> onRenameSelectedClick()
            SelectionAction.DELETE -> reduceState { copy(pendingBulkDelete = true) }
        }
    }

    private fun onDeleteSelectedDismissed() {
        reduceState { copy(pendingBulkDelete = false) }
    }

    private fun onDeleteSelectedConfirmed() {
        val selection = currentState.selection as? SelectionState.Active
        val items = currentVisibleItems()
        reduceState { copy(pendingBulkDelete = false, selection = SelectionState.Idle) }
        if (selection == null) return
        launchOnIO {
            selection.selectedIds.forEach { id ->
                when (val item = items.find { it.id == id }) {
                    is BrowserListItem.NotesBrowserFolderItem -> folderInteractor.delete(item.id)
                    is BrowserListItem.NotesBrowserNoteItem -> noteInteractor.delete(item.id)
                    null -> Unit
                }
            }
        }
    }

    private fun loadCurrentFolder() {
        browseJob?.cancel()
        val folderId = currentState.currentFolderId
        val sortOrder = currentState.sortOrder
        browseJob = combine(
                foldersListInteractor.getChildFolders(folderId, sortOrder),
                notesListInteractor.getNotes(folderId, sortOrder),
        ) { folders, notes ->
            val foldersBrowserItems: List<BrowserListItem> = folders.map { it.toBrowserItem() }
            val notesBrowserItems: List<BrowserListItem> = notes.map { it.toBrowserItem() }
            (foldersBrowserItems + notesBrowserItems).sortedWith(browserComparator(sortOrder))
        }
            .onEach { items ->
                reduceState { copy(browserListState = BrowserListState.Loaded(items)) }
            }
            .flowOnIO()
            .launchIn(viewModelScope)
    }

    private fun FolderVO.toBrowserItem(): BrowserListItem.NotesBrowserFolderItem {
        return BrowserListItem.NotesBrowserFolderItem(
                id = id,
                parentId = parentId,
                createdAt = createdAt,
                order = order,
                name = name,
        )
    }

    private fun NoteVO.toBrowserItem(): BrowserListItem.NotesBrowserNoteItem {
        return BrowserListItem.NotesBrowserNoteItem(
                id = id,
                createdAt = createdAt,
                order = order,
                title = title.takeIf { it.isNotBlank() },
                body = body.takeIf { it.isNotBlank() },
                color = color.argb,
                folderId = folderId,
        )
    }

    private fun toggleSelect(itemId: String) {
        val selection = currentState.selection as? SelectionState.Active ?: return
        val newIds = selection.selectedIds.toMutableSet()
        if (!newIds.add(itemId)) {
            newIds.remove(itemId)
        }
        reduceState {
            copy(
                    selection = if (newIds.isEmpty()) {
                        SelectionState.Idle
                    } else {
                        SelectionState.Active(
                                selectedIds = newIds,
                                availableActions = availableActions(newIds),
                        )
                    }
            )
        }
    }

    private fun availableActions(selectedIds: Set<String>): Set<SelectionAction> {
        val actions = mutableSetOf(SelectionAction.DELETE)
        val singleItem = selectedIds.singleOrNull()?.let { id -> currentVisibleItems().find { it.id == id } }
        if (singleItem is BrowserListItem.NotesBrowserFolderItem) {
            actions += SelectionAction.RENAME
        }
        return actions
    }

    private fun currentVisibleItems(): List<BrowserListItem> {
        val search = currentState.searchState
        return if (search is SearchState.Active) {
            search.results
        } else {
            (currentState.browserListState as? BrowserListState.Loaded)?.items.orEmpty()
        }
    }

    private fun moveToFolder(folderId: String?) {
        reduceState {
            copy(
                    browserListState = BrowserListState.Idle,
                    selection = SelectionState.Idle,
                    searchState = SearchState.Idle,
            )
        }
        launchOnIO {
            val breadcrumb = buildBreadcrumb(folderId)
            reduceState {
                copy(
                        currentFolderId = folderId,
                        breadcrumb = breadcrumb,
                        canNavigateBack = backStack.isNotEmpty(),
                )
            }
            loadCurrentFolder()
        }
    }

    private suspend fun buildBreadcrumb(folderId: String?): List<FolderBreadcrumbItem> {
        if (folderId == null) return emptyList()
        return folderInteractor.getFolderPath(folderId).map { FolderBreadcrumbItem(it.id, it.name) }
    }

    private fun onRenameSelectedClick() {
        val selection = currentState.selection as? SelectionState.Active
        val id = selection?.selectedIds?.singleOrNull()
        val item = id?.let { itemId -> currentVisibleItems().find { it.id == itemId } }
        val folder = item as? BrowserListItem.NotesBrowserFolderItem
        if (folder != null) {
            reduceState { copy(selection = SelectionState.Idle) }
            sendEffect { NotesBrowserEffect.NavigateToEditFolder(folder.id) }
        }
    }

    private fun browserComparator(sortOrder: NotesSortOrder): Comparator<BrowserListItem> {
        return when (sortOrder) {
            NotesSortOrder.NEWEST_FIRST -> compareByDescending { it.createdAt }
            NotesSortOrder.OLDEST_FIRST -> compareBy { it.createdAt }
            NotesSortOrder.CUSTOM -> compareBy<BrowserListItem> { it.order }.thenByDescending { it.createdAt }
        }
    }
}
