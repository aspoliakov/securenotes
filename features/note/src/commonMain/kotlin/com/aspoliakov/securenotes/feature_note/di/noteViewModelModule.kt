package com.aspoliakov.securenotes.feature_note.di

import com.aspoliakov.securenotes.feature_note.presentation.NoteState
import com.aspoliakov.securenotes.feature_note.presentation.NoteViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val noteViewModelModule = module {
    viewModel { params ->
        val noteId = params.getOrNull<String>()
        NoteViewModel(
                initialState = NoteState(
                        noteId = noteId,
                        newNote = noteId == null,
                ),
                noteInteractor = get(),
        )
    }
}
