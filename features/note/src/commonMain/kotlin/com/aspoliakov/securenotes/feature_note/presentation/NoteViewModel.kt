package com.aspoliakov.securenotes.feature_note.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.domain_notes.NotesCreateInteractor

/**
 * Project SecureNotes
 */

class NoteViewModel(
        initialState: NoteState,
        private val notesCreateInteractor: NotesCreateInteractor,
) : MviViewModel<NoteState, NoteEffect, NoteIntent>(initialState) {

    override fun onCleared() {
        notesCreateInteractor.addNote(
                title = currentState.title,
                body = currentState.body,
        )
        super.onCleared()
    }

    override fun handleIntent(intent: NoteIntent) {
        when (intent) {
            is NoteIntent.OnTitleChanged -> onTitleChanged(intent)
            is NoteIntent.OnBodyChanged -> onBodyChanged(intent)
        }
    }

    private fun onTitleChanged(intent: NoteIntent.OnTitleChanged) {
        reduceState { copy(title = intent.text) }
    }

    private fun onBodyChanged(intent: NoteIntent.OnBodyChanged) {
        reduceState { copy(body = intent.text) }
    }
}
