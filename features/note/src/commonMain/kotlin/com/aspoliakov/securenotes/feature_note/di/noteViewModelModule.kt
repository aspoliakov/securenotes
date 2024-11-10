package com.aspoliakov.securenotes.feature_note.di

import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import com.aspoliakov.securenotes.feature_note.presentation.NoteState
import com.aspoliakov.securenotes.feature_note.presentation.NoteViewModel
import kotlinx.coroutines.runBlocking
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val noteViewModelModule = module {
    viewModel { params ->
        val noteId = params.getOrNull<String>()
        val noteInteractor: NoteInteractor = get()
        val noteData = runBlocking { if (noteId != null) noteInteractor.getById(noteId) else null }
        NoteViewModel(
                initialState = NoteState(
                        noteId = noteId,
                        newNote = noteId == null,
                        title = noteData?.title ?: "",
                        body = noteData?.body ?: "",
                ),
                noteInteractor = noteInteractor,
        )
    }
}
