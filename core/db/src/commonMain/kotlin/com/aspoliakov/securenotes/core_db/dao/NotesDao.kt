package com.aspoliakov.securenotes.core_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.aspoliakov.securenotes.core_db.model.NoteDB
import kotlinx.coroutines.flow.Flow

/**
 * Project SecureNotes
 */

@Dao
interface NotesDao : BaseDao<NoteDB> {

    companion object {
        private const val TABLE = "notes"
    }

    @Query("SELECT * FROM $TABLE ORDER BY created_at DESC")
    fun selectAllByCreatedAtDesc(): Flow<List<NoteDB>>

    @Query("SELECT * FROM $TABLE ORDER BY created_at DESC")
    suspend fun searchAllByCreatedAtDesc(): List<NoteDB>
}
