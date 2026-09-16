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

    @Query("SELECT * FROM $TABLE WHERE folder_id IS :folderId ORDER BY created_at DESC")
    fun selectByFolderIdOrderByCreatedAtDesc(folderId: String?): Flow<List<NoteDB>>

    @Query("SELECT * FROM $TABLE WHERE note_id = :noteId")
    suspend fun selectById(noteId: String): NoteDB?

    @Query("SELECT * FROM $TABLE WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%' ORDER BY created_at DESC")
    suspend fun searchAllByCreatedAtDesc(query: String): List<NoteDB>

    @Query("UPDATE $TABLE SET title = :title, body = :body, color = :color WHERE note_id = :noteId")
    suspend fun updateNote(noteId: String, title: String?, body: String?, color: Long?)

    @Query("UPDATE $TABLE SET folder_id = :folderId WHERE note_id = :noteId")
    suspend fun updateFolder(noteId: String, folderId: String?)

    @Query("DELETE FROM $TABLE WHERE note_id = :noteId")
    suspend fun delete(noteId: String)

    @Query("DELETE FROM $TABLE WHERE folder_id = :folderId")
    suspend fun deleteByFolderId(folderId: String)

    @Query("DELETE FROM $TABLE")
    suspend fun deleteAll()
}
