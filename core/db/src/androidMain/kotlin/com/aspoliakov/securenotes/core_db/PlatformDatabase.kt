package com.aspoliakov.securenotes.core_db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Project SecureNotes
 */

internal class PlatformDatabase(private val context: Context) {

    fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = context.getDatabasePath("main_database.db")
        return Room.databaseBuilder<AppDatabase>(
                context = context,
                name = dbFile.absolutePath,
        )
    }
}
