package com.aspoliakov.securenotes.domain

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_base.util.flowOnIO
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.event_bus.SyncStackEventBus
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.domain_notes.NoteInteractor
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Project SecureNotes
 */

class SyncStackInteractor(
        private val syncStackDao: SyncStackDao,
        private val syncStackEventBus: SyncStackEventBus,
        private val noteInteractor: NoteInteractor,
) {

    private val syncStackJobs: MutableMap<String, Job> = mutableMapOf()

    fun sync() {
        IOScope().launch {
            syncStackDao.selectAll().forEach { syncItem(it) }
            observe()
        }
    }

    private fun observe() {
        syncStackEventBus.observe()
                .onEach { itemId ->
                    val syncStackDB = syncStackDao.selectById(itemId)
                    if (syncStackDB != null) {
                        syncItem(syncStackDB)
                    }
                }
                .flowOnIO()
                .launchIn(IOScope())
    }

    private fun syncItem(syncStackDB: SyncStackDB) {
        val itemId = syncStackDB.itemId
        val currentSyncJob = syncStackJobs[itemId]
        currentSyncJob?.cancel()
        syncStackJobs[itemId] = IOScope().launch {
            when (syncStackDB.itemType) {
                SyncStackDB.ItemType.NOTE -> runCatching {
                    noteInteractor.syncChanges(itemId)
                }
                        .onSuccess {
                            Napier.d("Note [$itemId] changes successfully synced")
                            syncStackDao.delete(itemId)
                        }
                        .onFailure {
                            Napier.e("Error syncing note [$itemId]: $it")
                        }
            }
        }
    }
}
