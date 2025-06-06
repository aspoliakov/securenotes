package com.aspoliakov.securenotes.feature_note.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State

/**
 * Project SecureNotes
 */

data class NoteState(
        val noteId: String? = null,
        val newNote: Boolean = true,
        val title: String = "",
        val body: String = "",
) : State()

sealed class NoteEffect : Effect() {
    data object Close : NoteEffect()
}

sealed class NoteIntent : Intent() {
    data object OnDeleteClick : NoteIntent()
    data class OnTitleChanged(val text: String) : NoteIntent()
    data class OnBodyChanged(val text: String) : NoteIntent()
}
