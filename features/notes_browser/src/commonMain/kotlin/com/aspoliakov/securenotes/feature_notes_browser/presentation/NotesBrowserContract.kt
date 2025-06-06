package com.aspoliakov.securenotes.feature_notes_browser.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State
import com.aspoliakov.securenotes.domain_notes.model.NotesListItem

/**
 * Project SecureNotes
 */

data class NotesBrowserState(
        val notesListState: NotesListState = NotesListState.Idle,
        val notesListFilteredState: NotesListState = NotesListState.Idle,
        val searchState: SearchState = SearchState.Idle,
) : State()

sealed class NotesListState {
    data object Idle : NotesListState()
    data object Loading : NotesListState()
    data class Loaded(val notesList: List<NotesListItem>) : NotesListState()
}

sealed class SearchState {
    data object Idle : SearchState()
    data class Searching(val query: String) : SearchState()
    data class Completed(val query: String) : SearchState()
}

sealed class NotesBrowserEffect : Effect() {
    data class ShowSnackbar(val message: String) : NotesBrowserEffect()
}

sealed class NotesBrowserIntent : Intent() {
    data class OnSearch(val query: String) : NotesBrowserIntent()
}
