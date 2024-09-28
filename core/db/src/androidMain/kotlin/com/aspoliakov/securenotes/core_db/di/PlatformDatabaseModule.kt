package com.aspoliakov.securenotes.core_db.di

import com.aspoliakov.securenotes.core_db.PlatformDatabase
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

internal actual val platformDatabaseModule = module {
    single { PlatformDatabase(get()).getDatabaseBuilder() }
}
