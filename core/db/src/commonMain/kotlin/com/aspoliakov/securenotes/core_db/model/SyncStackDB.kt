package com.aspoliakov.securenotes.core_db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Project SecureNotes
 */

@Entity(
        tableName = "sync_stack",
        indices = [
            Index(value = ["item_id"], unique = true)
        ]
)
data class SyncStackDB(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        var id: Long = 0,

        @ColumnInfo(name = "item_id")
        var itemId: String,

        @ColumnInfo(name = "item_type")
        var itemType: ItemType,
) {
    enum class ItemType {
        NOTE,
    }
}
