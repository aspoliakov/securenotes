package com.aspoliakov.securenotes.feature_notes_browser.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State
import com.aspoliakov.securenotes.domain_user_state.model.NotesViewMode

/**
 * Project SecureNotes
 */

internal data class NotesBrowserState(
        val currentFolderId: String? = null,
        val breadcrumb: List<FolderBreadcrumbItem> = emptyList(),
        val canNavigateBack: Boolean = false,
        val browserListState: BrowserListState = BrowserListState.Idle,
        val notesViewMode: NotesViewMode = NotesViewMode.LIST,
        val searchState: SearchState = SearchState.Idle,
        val selection: SelectionState = SelectionState.Idle,
        val pendingBulkDelete: Boolean = false,
) : State()

internal interface BrowsableItem {
    val id: String
    val createdAt: Long
}

internal data class NotesBrowserFolderItem(
        override val id: String,
        val parentId: String?,
        override val createdAt: Long,
        val name: String?,
) : BrowsableItem

internal data class NotesBrowserNoteItem(
        override val id: String,
        override val createdAt: Long,
        val title: String?,
        val body: String?,
        val color: Long?,
        val folderId: String?,
) : BrowsableItem

internal sealed class BrowserListItem : BrowsableItem {

    data class FolderRow(val folder: NotesBrowserFolderItem) : BrowserListItem() {
        override val id: String = folder.id
        override val createdAt: Long = folder.createdAt
    }

    data class NoteRow(val note: NotesBrowserNoteItem) : BrowserListItem() {
        override val id: String = note.id
        override val createdAt: Long = note.createdAt
    }
}

internal val browserChronologicalComparator: Comparator<BrowsableItem> = compareByDescending { it.createdAt }

internal sealed class BrowserListState {
    data object Idle : BrowserListState()
    data class Loaded(val items: List<BrowserListItem>) : BrowserListState()
}

internal sealed class SearchState {
    data object Idle : SearchState()
    data class Active(
            val query: String,
            val inProgress: Boolean,
            val results: List<BrowserListItem> = emptyList(),
    ) : SearchState()
}

enum class SelectionAction {
    RENAME,
    DELETE,
}

sealed class SelectionState {
    data object Idle : SelectionState()
    data class Active(
            val selectedIds: Set<String>,
            val availableActions: Set<SelectionAction> = emptySet(),
    ) : SelectionState()
}

data class FolderBreadcrumbItem(val id: String, val name: String)

sealed class NotesBrowserEffect : Effect() {
    data class ShowSnackbar(val message: String) : NotesBrowserEffect()
    data class NavigateToNote(val noteId: String) : NotesBrowserEffect()
    data class NavigateToEditFolder(val folderId: String) : NotesBrowserEffect()
}

sealed class NotesBrowserIntent : Intent() {
    data class OnSearch(val query: String) : NotesBrowserIntent()
    data object OnToggleViewMode : NotesBrowserIntent()
    data class OnItemClick(val itemId: String) : NotesBrowserIntent()
    data class OnItemLongClick(val itemId: String) : NotesBrowserIntent()
    data class OnBreadcrumbClick(val folderId: String?) : NotesBrowserIntent()
    data object OnNavigateBack : NotesBrowserIntent()
    data object OnExitSelection : NotesBrowserIntent()
    data class OnSelectionActionClick(val action: SelectionAction) : NotesBrowserIntent()
    data object OnDeleteSelectedConfirmed : NotesBrowserIntent()
    data object OnDeleteSelectedDismissed : NotesBrowserIntent()
}
