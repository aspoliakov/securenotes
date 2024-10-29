package com.aspoliakov.securenotes.core_db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Project SecureNotes
 */

@Entity(
        tableName = "notes",
        indices = [
            Index(value = ["note_id"], unique = true)
        ]
)
data class NoteDB(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        var _id: Long = 0,

        @ColumnInfo(name = "note_id")
        var id: String,

        @ColumnInfo(name = "created_at")
        var createdAt: Long,

        @ColumnInfo(name = "title")
        var title: String? = null,

        @ColumnInfo(name = "body")
        var body: String? = null,
)
