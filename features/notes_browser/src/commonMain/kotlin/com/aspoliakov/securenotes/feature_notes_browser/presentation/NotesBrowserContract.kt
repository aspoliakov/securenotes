package com.aspoliakov.securenotes.feature_notes_browser.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

/**
 * Project SecureNotes
 */

sealed class NotesBrowserState : MviState() {
    object Idle : NotesBrowserState()
}

sealed class NotesBrowserEffect : MviEffect() {
    data class ShowSnackbar(val message: String) : NotesBrowserEffect()
}

sealed class NotesBrowserIntent : MviIntent() {
}
