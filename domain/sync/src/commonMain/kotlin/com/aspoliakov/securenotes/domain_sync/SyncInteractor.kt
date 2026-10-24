package com.aspoliakov.securenotes.domain_sync

import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import com.aspoliakov.securenotes.domain_folders.FolderInteractor
import com.aspoliakov.securenotes.domain_folders.FoldersListInteractor
import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import com.aspoliakov.securenotes.domain_sync.network.SyncApiProvider
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Project SecureNotes
 *
 * Downloads notes and folders changed on the server since the last sync. The server numbers changes of
 * the user's data with revisions: the client sends the last one it has applied and gets what has changed
 * after it (deletions included) along with the new one; without one it gets everything, as a first sync.
 */

class SyncInteractor(
        private val syncApiProvider: SyncApiProvider,
        private val keyValueStorage: KeyValueStorage,
        private val foldersListInteractor: FoldersListInteractor,
        private val notesListInteractor: NotesListInteractor,
        private val folderInteractor: FolderInteractor,
        private val noteInteractor: NoteInteractor,
) {

    companion object {
        private const val SYNC_REVISION = "sync_revision"
    }

    // one at a time: a response applied after a newer one would move the revision back
    private val syncMutex = Mutex()

    suspend fun sync() {
        syncMutex.withLock {
            runCatching {
                val since = keyValueStorage.getLong(SYNC_REVISION).first()
                val response = syncApiProvider.provideApi().sync(since)
                // folders first: notes refer to them
                foldersListInteractor.applyServerChanges(response.folders)
                notesListInteractor.applyServerChanges(response.notes)
                folderInteractor.resetMissingParentIds()
                noteInteractor.resetMissingFolderIds()
                keyValueStorage.put(SYNC_REVISION, response.syncRevision)
            }
                .onFailure {
                    Napier.e("Error syncing notes and folders: $it")
                }
        }
    }
}
