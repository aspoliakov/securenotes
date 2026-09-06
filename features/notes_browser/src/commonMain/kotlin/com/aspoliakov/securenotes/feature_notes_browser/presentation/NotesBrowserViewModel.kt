package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_base.util.flowOnIO
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Project SecureNotes
 */

class NotesBrowserViewModel(
        initialState: NotesBrowserState,
        private val notesListInteractor: NotesListInteractor,
) : MviViewModel<NotesBrowserState, NotesBrowserEffect, NotesBrowserIntent>(initialState) {

    init {
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

    private var searchJob: Job? = null

    override fun handleIntent(intent: NotesBrowserIntent) {
        when (intent) {
            is NotesBrowserIntent.OnSearch -> searchNotes(intent.query)
            is NotesBrowserIntent.OnToggleViewMode -> toggleViewMode()
        }
    }

    private fun toggleViewMode() {
        val nextViewMode = when (currentState.notesViewMode) {
            NotesViewMode.LIST -> NotesViewMode.GRID
            NotesViewMode.GRID -> NotesViewMode.LIST
        }
        reduceState { copy(notesViewMode = nextViewMode) }
    }

    private fun searchNotes(query: String) {
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
                        ),
                )
            }
            val foundSearchList = notesListInteractor.searchNotesList(query)
            reduceState {
                copy(
                        searchState = SearchState.Active(
                                query = query,
                                results = foundSearchList,
                                inProgress = false,
                        ),
                )
            }
        }
    }
}
