package com.aspoliakov.securenotes.feature_notes_browser.di

import com.aspoliakov.securenotes.feature_notes_browser.presentation.NotesBrowserState
import com.aspoliakov.securenotes.feature_notes_browser.presentation.NotesBrowserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val notesBrowserViewModelModule = module {
    viewModel {
        NotesBrowserViewModel(
                initialState = NotesBrowserState(),
                notesListInteractor = get(),
        )
    }
}
