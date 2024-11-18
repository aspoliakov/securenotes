package com.aspoliakov.securenotes.domain_notes.di

import com.aspoliakov.securenotes.domain_notes.NoteCryptoInteractor
import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import com.aspoliakov.securenotes.domain_notes.network.NotesApi
import com.aspoliakov.securenotes.domain_notes.network.NotesFirestoreApi
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val notesDomainModule = module {
    single<NotesApi> { NotesFirestoreApi(get(), get(), Firebase.firestore) }
    single { NoteCryptoInteractor(get()) }
    single { NoteInteractor(get(), get(), get(), get(), get()) }
    single { NotesListInteractor(get(), get(), get()) }
}
