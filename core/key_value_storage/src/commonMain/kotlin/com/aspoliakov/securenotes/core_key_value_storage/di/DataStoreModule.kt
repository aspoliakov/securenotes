package com.aspoliakov.securenotes.core_key_value_storage.di

import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val dataStoreModule = module {
    single { KeyValueStorage(get()) }
    includes(platformDataStoreModule)
}

internal expect val platformDataStoreModule: Module
