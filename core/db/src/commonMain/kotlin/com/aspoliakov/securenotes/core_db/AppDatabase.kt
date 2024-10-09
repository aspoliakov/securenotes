package com.aspoliakov.securenotes.core_db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.model.NoteDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Project SecureNotes
 */

@Database(
        entities = [
            NoteDB::class
        ],
        version = 1,
        exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>

fun getDatabase(
        builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
            .addMigrations()
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
}
