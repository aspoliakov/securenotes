package com.aspoliakov.securenotes.domain

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_base.util.flowOnIO
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.event_bus.SyncStackEventBus
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.domain_folders.FolderInteractor
import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Project SecureNotes
 */

class SyncStackInteractor(
        private val syncStackDao: SyncStackDao,
        private val syncStackEventBus: SyncStackEventBus,
        private val noteInteractor: NoteInteractor,
        private val folderInteractor: FolderInteractor,
) {

    // uploads go one at a time: cancelling one in flight would not cancel it on the server
    private val syncMutex = Mutex()

    fun sync() {
        IOScope().launch {
            syncStackDao.selectAll().forEach { syncItem(it.itemId) }
            observe()
        }
    }

    private fun observe() {
        syncStackEventBus.observe()
            .onEach { itemId -> syncItem(itemId) }
            .flowOnIO()
            .launchIn(IOScope())
    }

    private fun syncItem(itemId: String) = IOScope().launch {
        syncMutex.withLock {
            // read under the lock: a repeated post finds the entry already synced and removed
            val syncStackDB = syncStackDao.selectById(itemId) ?: return@withLock
            when (syncStackDB.itemType) {
                SyncStackDB.ItemType.NOTE -> runCatching {
                    noteInteractor.syncChanges(itemId, syncStackDB.action)
                }
                    .onSuccess {
                        Napier.d("Note [$itemId] changes successfully synced")
                        // by row: an entry queued again during the upload has a new one and stays
                        syncStackDao.deleteByRowId(syncStackDB.id)
                    }
                    .onFailure {
                        Napier.e("Error syncing note [$itemId]: $it")
                    }
                SyncStackDB.ItemType.FOLDER -> runCatching {
                    folderInteractor.syncChanges(itemId, syncStackDB.action)
                }
                    .onSuccess {
                        Napier.d("Folder [$itemId] changes successfully synced")
                        // by row: an entry queued again during the upload has a new one and stays
                        syncStackDao.deleteByRowId(syncStackDB.id)
                    }
                    .onFailure {
                        Napier.e("Error syncing folder [$itemId]: $it")
                    }
            }
        }
    }
}
