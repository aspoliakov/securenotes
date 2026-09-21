package com.aspoliakov.securenotes.feature_note.presentation

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Project SecureNotes
 */

class NoteViewModel(
        initialState: NoteState,
        private val noteInteractor: NoteInteractor,
        private val folderId: String? = null,
) : MviViewModel<NoteState, NoteEffect, NoteIntent>(initialState) {

    private var saveChangesJob: Job? = null

    init {
        if (currentState.noteId == null) {
            launchOnIO {
                val newNoteId = noteInteractor.createNew(folderId = folderId)
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
            is NoteIntent.OnColorSelected -> onColorSelected(intent)
        }
    }

    private fun onNoteDelete() {
        val noteId = currentState.noteId
        if (noteId != null) {
            saveChangesJob?.cancel()
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

    private fun onColorSelected(intent: NoteIntent.OnColorSelected) {
        if (currentState.color == intent.color) return
        reduceState { copy(color = intent.color) }
        saveChanges()
    }

    private fun saveChanges() {
        val noteId = currentState.noteId ?: return
        saveChangesJob?.cancel()
        saveChangesJob = IOScope().launch {
            noteInteractor.saveChanges(
                    noteId = noteId,
                    title = currentState.title,
                    body = currentState.body,
                    color = currentState.color,
            )
        }
    }
}
