package com.aspoliakov.securenotes.domain_notes.di

import com.aspoliakov.securenotes.domain_notes.NoteCryptoInteractor
import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import com.aspoliakov.securenotes.domain_notes.network.NotesApiProvider
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val notesDomainModule = module {
    single<NotesApiProvider> { NotesApiProvider() }
    single { NoteCryptoInteractor(get()) }
    single { NoteInteractor(get(), get(), get(), get(), get(), get()) }
    single { NotesListInteractor(get(), get(), get(), get()) }
}
