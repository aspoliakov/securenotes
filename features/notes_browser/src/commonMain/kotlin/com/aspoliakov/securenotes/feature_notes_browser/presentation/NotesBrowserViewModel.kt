package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_base.util.flowOnIO
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_folders.FolderInteractor
import com.aspoliakov.securenotes.domain_folders.model.FolderVO
import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import com.aspoliakov.securenotes.domain_notes.model.NoteVO
import com.aspoliakov.securenotes.domain_user_state.UserPrefsInteractor
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
        private val noteInteractor: NoteInteractor,
        private val userPrefsInteractor: UserPrefsInteractor,
) : MviViewModel<NotesBrowserState, NotesBrowserEffect, NotesBrowserIntent>(initialState) {

    private data class BackEntry(val folderId: String?, val breadcrumb: List<FolderBreadcrumbItem>)

    private var browseJob: Job? = null
    private var searchJob: Job? = null
    private val backStack = ArrayDeque<BackEntry>()

    init {
        subscribeToCurrentFolder()
    }

    override fun handleIntent(intent: NotesBrowserIntent) {
        when (intent) {
            is NotesBrowserIntent.OnSearch -> searchAll(intent.query)
            is NotesBrowserIntent.OnToggleViewMode -> toggleViewMode()
            is NotesBrowserIntent.OnItemClick -> onItemClick(intent.itemId)
            is NotesBrowserIntent.OnItemLongClick -> onItemLongPress(intent.itemId)
            is NotesBrowserIntent.OnBreadcrumbClick -> navigateToFolder(intent.folderId)
            is NotesBrowserIntent.OnNavigateBack -> navigateBack()
            is NotesBrowserIntent.OnExitSelection -> reduceState { copy(selection = SelectionState.Idle) }
            is NotesBrowserIntent.OnSelectionActionClick -> onSelectionActionClick(intent.action)
            is NotesBrowserIntent.OnDeleteSelectedDismissed -> reduceState { copy(pendingBulkDelete = false) }
            is NotesBrowserIntent.OnDeleteSelectedConfirmed -> confirmDeleteSelected()
        }
    }

    private fun subscribeToCurrentFolder() {
        browseJob?.cancel()
        val folderId = currentState.currentFolderId
        browseJob = combine(
                folderInteractor.getChildFolders(folderId),
                notesListInteractor.getNotesList(folderId),
        ) { folders, notes ->
            val foldersBrowserItems: List<BrowserListItem> = folders.map {
                BrowserListItem.FolderRow(it.toBrowserItem())
            }
            val notesBrowserItems: List<BrowserListItem> = notes.map {
                BrowserListItem.NoteRow(it.toBrowserItem())
            }
            (foldersBrowserItems + notesBrowserItems).sortedWith(browserChronologicalComparator)
        }
            .onEach { items ->
                reduceState { copy(browserListState = BrowserListState.Loaded(items)) }
            }
            .flowOnIO()
            .launchIn(viewModelScope)
    }

    private fun currentVisibleItems(): List<BrowserListItem> {
        val search = currentState.searchState
        return if (search is SearchState.Active) {
            search.results
        } else {
            (currentState.browserListState as? BrowserListState.Loaded)?.items.orEmpty()
        }
    }

    private fun onItemClick(itemId: String) {
        if (currentState.selection is SelectionState.Active) {
            toggleSelect(itemId)
            return
        }
        when (val item = currentVisibleItems().find { it.id == itemId }) {
            is BrowserListItem.FolderRow -> navigateToFolder(item.folder.id)
            is BrowserListItem.NoteRow -> sendEffect { NotesBrowserEffect.NavigateToNote(item.note.id) }
            null -> Unit
        }
    }

    private fun onItemLongPress(itemId: String) {
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
        val previousEntry = BackEntry(currentState.currentFolderId, currentState.breadcrumb)
        reduceState {
            copy(
                    browserListState = BrowserListState.Idle,
                    selection = SelectionState.Idle,
                    searchState = SearchState.Idle,
            )
        }
        launchOnIO {
            val breadcrumb = folderId?.let { buildBreadcrumb(it) }.orEmpty()
            backStack.addLast(previousEntry)
            reduceState {
                copy(
                        currentFolderId = folderId,
                        breadcrumb = breadcrumb,
                        canNavigateBack = true,
                )
            }
            subscribeToCurrentFolder()
        }
    }

    private suspend fun buildBreadcrumb(folderId: String): List<FolderBreadcrumbItem> {
        return folderInteractor.getFolderPath(folderId).map { FolderBreadcrumbItem(it.id, it.name) }
    }

    private fun navigateBack() {
        val previous = backStack.removeLastOrNull() ?: return
        reduceState {
            copy(
                    currentFolderId = previous.folderId,
                    breadcrumb = previous.breadcrumb,
                    browserListState = BrowserListState.Idle,
                    selection = SelectionState.Idle,
                    canNavigateBack = backStack.isNotEmpty(),
            )
        }
        subscribeToCurrentFolder()
    }

    private fun toggleViewMode() {
        val nextViewMode = when (currentState.notesViewMode) {
            NotesViewMode.LIST -> NotesViewMode.GRID
            NotesViewMode.GRID -> NotesViewMode.LIST
        }
        reduceState { copy(notesViewMode = nextViewMode) }
        launchOnIO {
            userPrefsInteractor.setNotesViewMode(nextViewMode)
        }
    }

    private fun searchAll(query: String) {
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
            val foundFolders = folderInteractor.searchFolders(query)
            val foundNotes = notesListInteractor.searchNotesList(query)
            val foundFoldersBrowserItems: List<BrowserListItem> = foundFolders.map {
                BrowserListItem.FolderRow(it.toBrowserItem())
            }
            val foundNotesBrowserItems: List<BrowserListItem> = foundNotes.map {
                BrowserListItem.NoteRow(it.toBrowserItem())
            }
            val sortedResults = (foundFoldersBrowserItems + foundNotesBrowserItems)
                .sortedWith(browserChronologicalComparator)
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
        if (singleItem is BrowserListItem.FolderRow) {
            actions += SelectionAction.RENAME
        }
        return actions
    }

    private fun onSelectionActionClick(action: SelectionAction) {
        when (action) {
            SelectionAction.RENAME -> onRenameSelectedClick()
            SelectionAction.DELETE -> reduceState { copy(pendingBulkDelete = true) }
        }
    }

    private fun onRenameSelectedClick() {
        val selection = currentState.selection as? SelectionState.Active
        val id = selection?.selectedIds?.singleOrNull()
        val item = id?.let { itemId -> currentVisibleItems().find { it.id == itemId } }
        val folder = (item as? BrowserListItem.FolderRow)?.folder
        if (folder != null) {
            reduceState { copy(selection = SelectionState.Idle) }
            sendEffect { NotesBrowserEffect.NavigateToEditFolder(folder.id) }
        }
    }

    private fun confirmDeleteSelected() {
        val selection = currentState.selection as? SelectionState.Active
        val items = currentVisibleItems()
        reduceState { copy(pendingBulkDelete = false, selection = SelectionState.Idle) }
        if (selection == null) return
        launchOnIO {
            selection.selectedIds.forEach { id ->
                when (val item = items.find { it.id == id }) {
                    is BrowserListItem.FolderRow -> folderInteractor.delete(item.folder.id)
                    is BrowserListItem.NoteRow -> noteInteractor.delete(item.note.id)
                    null -> Unit
                }
            }
        }
    }

    private fun FolderVO.toBrowserItem(): NotesBrowserFolderItem {
        return NotesBrowserFolderItem(id = id, parentId = parentId, createdAt = createdAt, name = name)
    }

    private fun NoteVO.toBrowserItem(): NotesBrowserNoteItem {
        return NotesBrowserNoteItem(
                id = id,
                createdAt = createdAt,
                title = title.takeIf { it.isNotBlank() },
                body = body.takeIf { it.isNotBlank() },
                color = color.argb,
                folderId = folderId,
        )
    }
}
