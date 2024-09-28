package com.aspoliakov.securenotes.core_db.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Project SecureNotes
 */

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entities: Collection<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrReplace(entity: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrReplace(entities: Collection<T>)
}
