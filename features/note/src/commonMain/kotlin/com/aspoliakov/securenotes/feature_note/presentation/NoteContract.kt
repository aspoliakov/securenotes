package com.aspoliakov.securenotes.feature_note.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

/**
 * Project SecureNotes
 */

data class NoteState(
        val noteId: String? = null,
        val newNote: Boolean = true,
        val title: String = "",
        val body: String = "",
) : MviState()

sealed class NoteEffect : MviEffect() {
    data object Close : NoteEffect()
}

sealed class NoteIntent : MviIntent() {
    data object OnDeleteClick : NoteIntent()
    data class OnTitleChanged(val text: String) : NoteIntent()
    data class OnBodyChanged(val text: String) : NoteIntent()
}
