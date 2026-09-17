package com.aspoliakov.securenotes.feature_notes_browser.di

import com.aspoliakov.securenotes.domain_user_state.UserPrefsInteractor
import com.aspoliakov.securenotes.feature_notes_browser.presentation.NotesBrowserState
import com.aspoliakov.securenotes.feature_notes_browser.presentation.NotesBrowserViewModel
import kotlinx.coroutines.runBlocking
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val notesBrowserViewModelModule = module {
    viewModel {
        val userPrefsInteractor: UserPrefsInteractor = get()
        val notesViewMode = runBlocking { userPrefsInteractor.getNotesViewMode() }
        NotesBrowserViewModel(
                initialState = NotesBrowserState(
                        notesViewMode = notesViewMode,
                ),
                notesListInteractor = get(),
                folderInteractor = get(),
                foldersListInteractor = get(),
                noteInteractor = get(),
                userPrefsInteractor = userPrefsInteractor,
        )
    }
}
