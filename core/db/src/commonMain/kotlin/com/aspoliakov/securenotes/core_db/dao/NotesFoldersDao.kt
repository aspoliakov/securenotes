package com.aspoliakov.securenotes.core_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.aspoliakov.securenotes.core_db.model.NoteFolderDB
import kotlinx.coroutines.flow.Flow

/**
 * Project SecureNotes
 */

@Dao
interface NotesFoldersDao : BaseDao<NoteFolderDB> {

    companion object {
        private const val TABLE = "notes_folders"
    }

    // region read

    @Query("SELECT * FROM $TABLE")
    fun selectAll(): Flow<List<NoteFolderDB>>

    @Query("SELECT * FROM $TABLE WHERE parent_id = :parentId ORDER BY created_at ASC limit :limit")
    fun selectAllByCreatedAtDesc(parentId: String, limit: Int): Flow<List<NoteFolderDB>>

    @Query("SELECT * FROM $TABLE WHERE id = :id LIMIT 1")
    fun selectById(id: String): Flow<NoteFolderDB>

    // endregion read

    // region update

    @Query("UPDATE $TABLE SET name = :name WHERE id = :id")
    suspend fun updateName(id: String, name: String)
}
