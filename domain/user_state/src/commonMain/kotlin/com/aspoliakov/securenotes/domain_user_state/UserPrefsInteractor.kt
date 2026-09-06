package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import com.aspoliakov.securenotes.domain_user_state.model.NotesViewMode
import kotlinx.coroutines.flow.firstOrNull

/**
 * Project SecureNotes
 */

class UserPrefsInteractor(
        private val keyValueStorage: KeyValueStorage,
) {

    companion object {
        private const val NOTES_VIEW_MODE = "notes_view_mode"
    }

    suspend fun getNotesViewMode(): NotesViewMode {
        return NotesViewMode.fromName(keyValueStorage.getString(NOTES_VIEW_MODE).firstOrNull())
    }

    suspend fun setNotesViewMode(notesViewMode: NotesViewMode) {
        keyValueStorage.put(NOTES_VIEW_MODE, notesViewMode.name)
    }
}
