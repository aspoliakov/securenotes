package com.aspoliakov.securenotes.domain_notes.di

import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val notesDomainModule = module {
    single { NoteInteractor(get(), get(), get()) }
    single { NotesListInteractor(get()) }
}
