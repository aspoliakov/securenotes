package com.aspoliakov.securenotes.domain_notes

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_db.dao.FolderDao
import com.aspoliakov.securenotes.core_db.dao.NotesDao
import com.aspoliakov.securenotes.core_db.model.NoteDB
import com.aspoliakov.securenotes.domain_folders.FolderInteractor
import com.aspoliakov.securenotes.domain_notes.model.NoteColor
import com.aspoliakov.securenotes.domain_notes.model.NoteVO
import com.aspoliakov.securenotes.domain_notes.network.NotesApiProvider
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Project SecureNotes
 */

class NotesListInteractor(
        private val notesDao: NotesDao,
        private val folderDao: FolderDao,
        private val notesApiProvider: NotesApiProvider,
        private val userStateInteractor: UserStateInteractor,
        private val noteCryptoInteractor: NoteCryptoInteractor,
        private val folderInteractor: FolderInteractor,
) {

    fun getNotesList(folderId: String?): Flow<List<NoteVO>> {
        return notesDao.selectByFolderIdOrderByCreatedAtDesc(folderId)
                .map { notesList -> notesList.map(this::mapNoteDBToNoteVO) }
                .also { sync() }
    }

    suspend fun searchNotesList(query: String): List<NoteVO> {
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
            folderInteractor.syncFolders()
            val notes = notesApiProvider.provideApi().getAllNotes(
                    token = userStateInteractor.getUserToken() ?: throw IllegalStateException(),
            )
                    .notes
                    .map {
                        val notePayload = noteCryptoInteractor.decrypt(it.payload)
                        val resolvedFolderId = it.folderId?.takeIf { folderId ->
                            folderDao.selectById(folderId) != null
                        }
                        NoteDB(
                                noteId = it.noteId,
                                folderId = resolvedFolderId,
                                createdAt = 1, // TODO
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
