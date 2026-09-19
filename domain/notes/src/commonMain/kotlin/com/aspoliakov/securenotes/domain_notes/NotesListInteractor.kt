package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_db.dao.FolderDao
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.model.NoteDB
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.domain_folders.FoldersListInteractor
import com.aspoliakov.securenotes.domain_notes.model.NoteColor
import com.aspoliakov.securenotes.domain_notes.model.NoteVO
import com.aspoliakov.securenotes.domain_notes.network.NotesApiProvider
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import com.aspoliakov.securenotes.domain_user_state.model.NotesSortOrder
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * Project SecureNotes
 */

class NotesListInteractor(
        private val notesDao: NotesDao,
        private val folderDao: FolderDao,
        private val syncStackDao: SyncStackDao,
        private val notesApiProvider: NotesApiProvider,
        private val userStateInteractor: UserStateInteractor,
        private val noteCryptoInteractor: NoteCryptoInteractor,
        private val foldersListInteractor: FoldersListInteractor,
) {

    fun getNotes(folderId: String?, sortOrder: NotesSortOrder): Flow<List<NoteVO>> {
        val notesFlow = when (sortOrder) {
            NotesSortOrder.NEWEST_FIRST -> notesDao.selectByFolderIdOrderByCreatedAtDesc(folderId)
            NotesSortOrder.OLDEST_FIRST -> notesDao.selectByFolderIdOrderByCreatedAtAsc(folderId)
        }
        return notesFlow
                .map { notesList -> notesList.map(this::mapNoteDBToNoteVO) }
                .also { sync() }
    }

    suspend fun searchNotes(query: String): List<NoteVO> {
        return notesDao.searchAllByCreatedAtDesc(query)
                .map(this::mapNoteDBToNoteVO)
    }

    private fun mapNoteDBToNoteVO(noteDB: NoteDB): NoteVO {
        return NoteVO(
                id = noteDB.noteId,
                createdAt = noteDB.createdAt,
                folderId = noteDB.folderId,
                title = noteDB.title ?: "",
                body = noteDB.body ?: "",
                color = NoteColor.fromArgb(noteDB.color),
        )
    }

    private fun sync() = IOScope().launch {
        runCatching {
            // folders must be synced first so that folder_id resolution below reflects
            // the freshest known folder set (orphaned folder_id falls back to root)
            foldersListInteractor.syncFolders()
            // notes with a pending sync-stack entry are not yet reflected on the server (or are
            // queued for local deletion); overwriting them here with stale server data would
            // resurrect a locally deleted note or silently discard an unsynced local edit, so
            // they are left untouched until their own pending sync completes
            val pendingIds = syncStackDao.selectIdsByItemType(SyncStackDB.ItemType.NOTE).toSet()
            val notes = notesApiProvider.provideApi().getAllNotes(
                    token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
            )
                    .notes
                    .filterNot { it.noteId in pendingIds }
                    .map {
                        val notePayload = noteCryptoInteractor.decrypt(it.payload)
                        val resolvedFolderId = it.folderId?.takeIf { folderId ->
                            folderDao.selectById(folderId) != null
                        }
                        NoteDB(
                                noteId = it.noteId,
                                folderId = resolvedFolderId,
                                createdAt = it.createdAt.toEpochMillis(),
                                title = notePayload.title,
                                body = notePayload.body,
                                color = NoteColor.fromArgb(notePayload.color).argb,
                        )
                    }
            notesDao.insertOrReplace(notes)
        }
                .onFailure {
                    Napier.e("Error syncing notes: $it")
                }
    }
}

private fun String.toEpochMillis(): Long {
    return LocalDateTime.parse(this).toInstant(TimeZone.UTC).toEpochMilliseconds()
}
