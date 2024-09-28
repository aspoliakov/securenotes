package com.aspoliakov.securenotes.core_db.di

import com.aspoliakov.securenotes.core_db.AppDatabase
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.dao.NotesFoldersDao
import com.aspoliakov.securenotes.core_db.getDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val databaseModule = module {
    single { getDatabase(get()) }
    single<NotesFoldersDao> { get<AppDatabase>().foldersDao() }
    single<NotesDao> { get<AppDatabase>().notesDao() }
    includes(platformDatabaseModule)
}

internal expect val platformDatabaseModule: Module
