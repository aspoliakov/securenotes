package com.aspoliakov.securenotes.core_db.dao

import androidx.room.Dao
import androidx.room.Query
import com.aspoliakov.securenotes.core_db.model.SyncStackDB

/**
 * Project SecureNotes
 */

@Dao
interface SyncStackDao : BaseDao<SyncStackDB> {

    companion object {
        private const val TABLE = "sync_stack"
    }

    @Query("SELECT * FROM $TABLE")
    suspend fun selectAll(): List<SyncStackDB>

    @Query("SELECT * FROM $TABLE WHERE item_id = :itemId")
    suspend fun selectById(itemId: String): SyncStackDB?

    @Query("DELETE FROM $TABLE WHERE item_id = :itemId")
    suspend fun delete(itemId: String)

    @Query("DELETE FROM $TABLE")
    suspend fun deleteAll()
}
