package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_notes.NotesCreateInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Project SecureNotes
 */

class NotesBrowserViewModel(
        initialState: NotesBrowserState,
        private val notesListInteractor: NotesListInteractor,
        private val notesCreateInteractor: NotesCreateInteractor,
) : MviViewModel<NotesBrowserState, NotesBrowserEffect, NotesBrowserIntent>(initialState) {

    init {
        notesListInteractor.getNotesList()
                .onEach { notesList ->
                    delay(1000)
                    reduceState {
                        copy(
                                notesListState = NotesListState.Loaded(
                                        notesList = notesList,
                                )
                        )
                    }
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: NotesBrowserIntent) {
        when (intent) {
            is NotesBrowserIntent.CreateNote -> createNote()
            is NotesBrowserIntent.OnSearch -> searchNotes(intent.query)
        }
    }

    private fun createNote() = launchOnIO {
        notesCreateInteractor.addMockNote()
    }

    private fun searchNotes(query: String) = launchOnIO {
        reduceState { copy(searchState = SearchState.Searching(query)) }
        delay(500)
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
