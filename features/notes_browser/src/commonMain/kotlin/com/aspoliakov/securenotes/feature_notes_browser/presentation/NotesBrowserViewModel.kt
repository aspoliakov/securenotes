package com.aspoliakov.securenotes.feature_notes_browser.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel

/**
 * Project SecureNotes
 */

class NotesBrowserViewModel(
    initialState: NotesBrowserState,
) : MviViewModel<NotesBrowserState, NotesBrowserEffect, NotesBrowserIntent>(initialState) {

    override fun handleIntent(intent: NotesBrowserIntent) {

    }
}
