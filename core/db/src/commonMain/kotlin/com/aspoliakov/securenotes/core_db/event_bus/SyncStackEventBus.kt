package com.aspoliakov.securenotes.core_db.event_bus

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Project SecureNotes
 */

class SyncStackEventBus {

    private val _sharedFlow = MutableSharedFlow<String>()
    private val sharedFlow = _sharedFlow.asSharedFlow()

    suspend fun post(itemId: String) {
        _sharedFlow.emit(itemId)
    }

    fun observe(): Flow<String> {
        return sharedFlow
    }
}
