package com.aspoliakov.securenotes.feature_notes_browser.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState
import com.aspoliakov.securenotes.domain_notes.data.NotesListItem

/**
 * Project SecureNotes
 */

sealed class NotesBrowserState : MviState() {
    data object Init : NotesBrowserState()
    data class Loaded(
            val notesList: List<NotesListItem>,
    ) : NotesBrowserState()
}

sealed class NotesBrowserEffect : MviEffect() {
    data class ShowSnackbar(val message: String) : NotesBrowserEffect()
}

sealed class NotesBrowserIntent : MviIntent() {
    data object CreateNote : NotesBrowserIntent()
}
