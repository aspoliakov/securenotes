package com.aspoliakov.securenotes.feature_note.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

/**
 * Project SecureNotes
 */

sealed class NoteState : MviState() {
    data object Idle : NoteState()
}

sealed class NoteEffect : MviEffect() {
    data class ShowSnackbar(val message: String) : NoteEffect()
}

sealed class NoteIntent : MviIntent() {
}
