package com.aspoliakov.securenotes.feature_note.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_notes.NoteInteractor

/**
 * Project SecureNotes
 */

class NoteViewModel(
        initialState: NoteState,
        private val noteInteractor: NoteInteractor,
) : MviViewModel<NoteState, NoteEffect, NoteIntent>(initialState) {

    override fun initData() {
        if (currentState.noteId == null) {
            launchOnIO {
                val newNoteId = noteInteractor.createNew()
                reduceState { copy(noteId = newNoteId) }
            }
        }
    }

    override fun onCleared() {
        val noteId = currentState.noteId
        if (currentState.newNote && noteId != null) {
            noteInteractor.undoCreation(noteId)
        }
        super.onCleared()
    }

    override fun handleIntent(intent: NoteIntent) {
        when (intent) {
            is NoteIntent.OnDeleteClick -> onNoteDelete()
            is NoteIntent.OnTitleChanged -> onTitleChanged(intent)
            is NoteIntent.OnBodyChanged -> onBodyChanged(intent)
        }
    }

    private fun onNoteDelete() {
        val noteId = currentState.noteId
        if (noteId != null) {
            launchOnIO {
                noteInteractor.delete(noteId)
                sendEffect { NoteEffect.Close }
            }
        }
    }

    private fun onTitleChanged(intent: NoteIntent.OnTitleChanged) {
        reduceState { copy(title = intent.text) }
        saveChanges()
    }

    private fun onBodyChanged(intent: NoteIntent.OnBodyChanged) {
        reduceState { copy(body = intent.text) }
        saveChanges()
    }

    private fun saveChanges() {
        val noteId = currentState.noteId
        if (noteId != null) {
            noteInteractor.saveChanges(
                    noteId = noteId,
                    title = currentState.title,
                    body = currentState.body,
            )
        }
    }
}
