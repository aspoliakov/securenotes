package com.aspoliakov.securenotes.core_db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Project SecureNotes
 */

@Entity(
        tableName = "notes_folders",
        indices = [
            Index(value = ["id"], unique = true)
        ]
)
data class NoteFolderDB(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        var _id: Long = 0,

        @ColumnInfo(name = "id")
        var id: String,

        @ColumnInfo(name = "parent_id")
        var parent_id: String = ROOT_FOLDER,

        @ColumnInfo(name = "created_at")
        var createdAt: Long,

        @ColumnInfo(name = "edited_at")
        var editedAt: Long? = null,

        @ColumnInfo(name = "name")
        var name: String,

        @ColumnInfo(name = "folder_order")
        var order: Int = -1
) {
    companion object {
        const val ROOT_FOLDER = "root"
    }
}
