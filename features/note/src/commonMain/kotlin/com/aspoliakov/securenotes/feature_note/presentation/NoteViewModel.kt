package com.aspoliakov.securenotes.feature_note.presentation

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_base.util.flowOnIO
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Project SecureNotes
 */

class NoteViewModel(
        initialState: NoteState,
        private val noteInteractor: NoteInteractor,
        private val folderId: String? = null,
) : MviViewModel<NoteState, NoteEffect, NoteIntent>(initialState) {

    companion object {
        private const val DRAFT_SAVE_PERIOD = 500L
    }

    private val noteActions = Mutex()

    init {
        val noteId = currentState.noteId
        if (noteId == null) {
            launchOnIO {
                val newNoteId = noteInteractor.createNew(folderId = folderId)
                reduceState { copy(noteId = newNoteId) }
                observeNote(newNoteId)
            }
        } else {
            observeNote(noteId)
        }
        observeDraft()
    }

    override fun onCleared() {
        val newNote = currentState.newNote
        runNoteAction { noteId ->
            saveDraft(noteId)
            if (newNote) {
                noteInteractor.undoCreation(noteId)
            }
            noteInteractor.syncChanges(noteId)
        }
        super.onCleared()
    }

    override fun handleIntent(intent: NoteIntent) {
        when (intent) {
            is NoteIntent.OnDeleteClick -> onNoteDelete()
            is NoteIntent.OnTitleChanged -> onTitleChanged(intent)
            is NoteIntent.OnBodyChanged -> onBodyChanged(intent)
            is NoteIntent.OnColorSelected -> onColorSelected(intent)
            is NoteIntent.OnScreenStopped -> onScreenStopped()
        }
    }

    private fun onNoteDelete() {
        runNoteAction { noteId ->
            noteInteractor.delete(noteId)
            sendEffect { NoteEffect.Close }
        }
    }

    private fun onTitleChanged(intent: NoteIntent.OnTitleChanged) {
        reduceState { copy(title = intent.text) }
    }

    private fun onBodyChanged(intent: NoteIntent.OnBodyChanged) {
        reduceState { copy(body = intent.text) }
    }

    private fun onColorSelected(intent: NoteIntent.OnColorSelected) {
        runNoteAction { noteInteractor.setColor(it, intent.color) }
    }

    private fun onScreenStopped() {
        if (currentState.newNote && currentState.title.isBlank() && currentState.body.isBlank()) return
        reduceState { copy(newNote = false) }
        runNoteAction { noteId ->
            saveDraft(noteId)
            noteInteractor.syncChanges(noteId)
        }
    }

    private fun observeNote(noteId: String) {
        noteInteractor.observeById(noteId)
            .onEach {
                reduceState {
                    copy(
                            color = it.color,
                            contentUpdatedAt = it.contentUpdatedAt,
                    )
                }
            }
            .flowOnIO()
            .launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun observeDraft() {
        state.map { it.title to it.body }
            .distinctUntilChanged()
            .drop(1)
            .sample(DRAFT_SAVE_PERIOD.milliseconds)
            .onEach { runNoteAction { noteId -> saveDraft(noteId) } }
            .launchIn(viewModelScope)
    }

    private suspend fun saveDraft(noteId: String) {
        noteInteractor.setTitle(noteId, currentState.title)
        noteInteractor.setBody(noteId, currentState.body)
    }

    private fun runNoteAction(action: suspend (noteId: String) -> Unit) {
        val noteId = currentState.noteId ?: return
        IOScope().launch {
            noteActions.withLock { action(noteId) }
        }
    }
}
