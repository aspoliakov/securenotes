package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_notes.NotesCreateInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Project SecureNotes
 */

class NotesBrowserViewModel(
        initialState: NotesBrowserState,
        notesListInteractor: NotesListInteractor,
        private val notesCreateInteractor: NotesCreateInteractor,
) : MviViewModel<NotesBrowserState, NotesBrowserEffect, NotesBrowserIntent>(initialState) {

    init {
        notesListInteractor.getNotesList()
                .onEach { notesList ->
                    val newState = when (val state = currentState) {
                        is NotesBrowserState.Init -> NotesBrowserState.Loaded(
                                notesList = notesList,
                        )
                        is NotesBrowserState.Loaded -> state.copy(
                                notesList = notesList,
                        )
                    }
                    reduceState { newState }
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: NotesBrowserIntent) {
        when (intent) {
            is NotesBrowserIntent.CreateNote -> createNote()
        }
    }

    private fun createNote() = launchOnIO {
        notesCreateInteractor.addMockNote()
    }
}
