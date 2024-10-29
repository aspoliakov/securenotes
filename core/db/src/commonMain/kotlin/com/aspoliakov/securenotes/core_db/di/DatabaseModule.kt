package com.aspoliakov.securenotes.core_db.di

import com.aspoliakov.securenotes.core_db.AppDatabase
import com.aspoliakov.securenotes.core_db.DatabaseManager
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.event_bus.SyncStackEventBus
import com.aspoliakov.securenotes.core_db.getDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val databaseModule = module {
    single { getDatabase(get()) }
    single<DatabaseManager> { DatabaseManager(get()) }
    single<NotesDao> { get<AppDatabase>().notesDao() }
    single<SyncStackDao> { get<AppDatabase>().syncStackDao() }
    single<SyncStackEventBus> { SyncStackEventBus() }
    includes(platformDatabaseModule)
}

internal expect val platformDatabaseModule: Module
