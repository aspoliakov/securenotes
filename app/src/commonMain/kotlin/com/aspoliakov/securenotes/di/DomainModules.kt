package com.aspoliakov.securenotes.di

import com.aspoliakov.securenotes.domain.SyncStackInteractor
import com.aspoliakov.securenotes.domain_notes.di.notesDomainModule
import com.aspoliakov.securenotes.domain_user_state.di.userStateDomainModule
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val appMainDomainModule = module {
    single {
        SyncStackInteractor(
                syncStackDao = get(),
                syncStackEventBus = get(),
                noteInteractor = get(),
        )
    }
}

val domainModule = listOf(
        appMainDomainModule,
        userStateDomainModule,
        notesDomainModule,
)
