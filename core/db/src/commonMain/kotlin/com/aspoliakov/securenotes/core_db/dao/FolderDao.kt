package com.aspoliakov.securenotes.core_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.aspoliakov.securenotes.core_db.model.FolderDB
import kotlinx.coroutines.flow.Flow

/**
 * Project SecureNotes
 */

@Dao
interface FolderDao : BaseDao<FolderDB> {

    companion object {
        private const val TABLE = "folders"
    }

    @Query("SELECT * FROM $TABLE WHERE parent_id IS :parentId ORDER BY created_at DESC")
    fun selectChildrenByParentIdOrderByCreatedAtDesc(parentId: String?): Flow<List<FolderDB>>

    @Query("SELECT * FROM $TABLE WHERE parent_id IS :parentId ORDER BY created_at ASC")
    fun selectChildrenByParentIdOrderByCreatedAtAsc(parentId: String?): Flow<List<FolderDB>>

    @Query("SELECT * FROM $TABLE ORDER BY created_at DESC")
    fun selectAll(): Flow<List<FolderDB>>

    @Query("SELECT * FROM $TABLE WHERE folder_id = :folderId")
    suspend fun selectById(folderId: String): FolderDB?

    @Query("SELECT * FROM $TABLE WHERE name LIKE '%' || :query || '%' ORDER BY created_at DESC")
    suspend fun searchByName(query: String): List<FolderDB>

    @Query("UPDATE $TABLE SET name = :name WHERE folder_id = :folderId")
    suspend fun updateName(folderId: String, name: String?)

    @Query("UPDATE $TABLE SET parent_id = :parentId WHERE folder_id = :folderId")
    suspend fun updateParent(folderId: String, parentId: String?)

    @Query("DELETE FROM $TABLE WHERE folder_id = :folderId")
    suspend fun delete(folderId: String)

    @Query("DELETE FROM $TABLE")
    suspend fun deleteAll()
}
