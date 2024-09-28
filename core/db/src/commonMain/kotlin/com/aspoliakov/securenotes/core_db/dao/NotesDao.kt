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

    //region read

    @Query("SELECT * FROM $TABLE")
    fun selectAll(): Flow<List<NoteDB>>

    @Query("SELECT * FROM $TABLE WHERE folder_id = :folderId ORDER BY created_at ASC limit :limit")
    fun selectAllByCreatedAtDesc(folderId: String, limit: Int): Flow<List<NoteDB>>

    //endregion
}
