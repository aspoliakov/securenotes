package com.aspoliakov.securenotes.core_key_value_storage.di

import com.aspoliakov.securenotes.core_key_value_storage.PlatformDataStore
import com.liftric.kvault.KVault
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

internal actual val platformDataStoreModule = module {
    single { PlatformDataStore().getDataStore() }
    single { KVault() }
}
