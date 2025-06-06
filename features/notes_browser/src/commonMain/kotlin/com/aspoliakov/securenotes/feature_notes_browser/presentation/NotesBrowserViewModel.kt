package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_base.util.flowOnIO
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Project SecureNotes
 */

class NotesBrowserViewModel(
        initialState: NotesBrowserState,
        private val notesListInteractor: NotesListInteractor,
) : MviViewModel<NotesBrowserState, NotesBrowserEffect, NotesBrowserIntent>(initialState) {

    override fun initData() {
        notesListInteractor.getNotesList()
                .onEach { notesList ->
                    reduceState {
                        copy(
                                notesListState = NotesListState.Loaded(
                                        notesList = notesList,
                                )
                        )
                    }
                }
                .flowOnIO()
                .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: NotesBrowserIntent) {
        when (intent) {
            is NotesBrowserIntent.OnSearch -> searchNotes(intent.query)
        }
    }

    private fun searchNotes(query: String) = launchOnIO {
        reduceState { copy(searchState = SearchState.Searching(query)) }
        val foundSearchList = notesListInteractor.searchNotesList(query)
        reduceState {
            copy(
                    searchState = SearchState.Completed(query),
                    notesListFilteredState = NotesListState.Loaded(
                            notesList = foundSearchList,
                    ),
            )
        }
    }
}
