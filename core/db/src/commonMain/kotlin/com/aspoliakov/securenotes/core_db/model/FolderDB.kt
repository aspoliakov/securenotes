package com.aspoliakov.securenotes.core_db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Project SecureNotes
 */

@Entity(
        tableName = "folders",
        indices = [
            Index(value = ["folder_id"], unique = true),
            Index(value = ["parent_id"]),
        ]
)
data class FolderDB(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        var id: Long = 0,

        @ColumnInfo(name = "folder_id")
        var folderId: String,

        @ColumnInfo(name = "parent_id")
        var parentId: String? = null,

        @ColumnInfo(name = "created_at")
        var createdAt: Long,

        @ColumnInfo(name = "name")
        var name: String? = null,

        @ColumnInfo(name = "order_index")
        var order: Double = 0.0,
)
