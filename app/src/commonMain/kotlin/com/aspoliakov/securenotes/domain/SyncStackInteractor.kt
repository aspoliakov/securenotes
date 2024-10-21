package com.aspoliakov.securenotes.domain

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_base.util.flowOnIO
import com.aspoliakov.securenotes.core_db.dao.SyncStackDao
import com.aspoliakov.securenotes.core_db.event_bus.SyncStackEventBus
import com.aspoliakov.securenotes.core_db.model.SyncStackDB
import com.aspoliakov.securenotes.domain_notes.NoteInteractor
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
            syncStackDao.selectAll().forEach { syncItemId(it) }
            observe()
        }
    }

    private fun observe() {
        syncStackEventBus.observe()
                .onEach { itemId ->
                    val syncStackDB = syncStackDao.selectById(itemId)
                    if (syncStackDB != null) {
                        syncItemId(syncStackDB)
                    }
                }
                .flowOnIO()
                .launchIn(IOScope())
    }

    private fun syncItemId(syncStackDB: SyncStackDB) {
        val currentSyncJob = syncStackJobs[syncStackDB.id]
        currentSyncJob?.cancel()
        syncStackJobs[syncStackDB.id] = IOScope().launch {
            when (syncStackDB.itemType) {
                SyncStackDB.ItemType.NOTE -> noteInteractor.syncChanges(syncStackDB.id)
            }
        }
    }
}
