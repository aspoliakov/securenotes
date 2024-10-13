package com.aspoliakov.securenotes.feature_notes_browser.di

import com.aspoliakov.securenotes.feature_notes_browser.presentation.NotesBrowserState
import com.aspoliakov.securenotes.feature_notes_browser.presentation.NotesBrowserViewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val noteBrowserViewModelModule = module {
    factory {
        NotesBrowserViewModel(
                initialState = NotesBrowserState(),
                notesListInteractor = get(),
                notesCreateInteractor = get(),
        )
    }
}
