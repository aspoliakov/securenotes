package com.aspoliakov.securenotes.feature_note.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel

/**
 * Project SecureNotes
 */

class NoteViewModel(
    initialState: NoteState,
) : MviViewModel<NoteState, NoteEffect, NoteIntent>(initialState) {

    override fun handleIntent(intent: NoteIntent) {

    }
}
