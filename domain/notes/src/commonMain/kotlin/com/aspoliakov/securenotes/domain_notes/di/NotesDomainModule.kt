package com.aspoliakov.securenotes.domain_notes.di

import com.aspoliakov.securenotes.domain_notes.NotesCreateInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val notesDomainModule = module {
    single { NotesCreateInteractor(get()) }
    single { NotesListInteractor(get()) }
}
