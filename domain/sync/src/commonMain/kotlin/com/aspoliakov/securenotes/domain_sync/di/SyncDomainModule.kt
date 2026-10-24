package com.aspoliakov.securenotes.domain_sync.di

import com.aspoliakov.securenotes.domain_sync.SyncInteractor
import com.aspoliakov.securenotes.domain_sync.network.SyncApiProvider
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val syncDomainModule = module {
    single { SyncApiProvider(get()) }
    single { SyncInteractor(get(), get(), get(), get(), get(), get()) }
}
