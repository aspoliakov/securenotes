package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import com.aspoliakov.securenotes.domain_user_state.model.AppThemeMode
import com.aspoliakov.securenotes.domain_user_state.model.NotesSortOrder
import com.aspoliakov.securenotes.domain_user_state.model.NotesViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Project SecureNotes
 */

class UserPrefsInteractor(
        private val keyValueStorage: KeyValueStorage,
) {

    companion object {
        private const val NOTES_VIEW_MODE = "notes_view_mode"
        private const val NOTES_SORT_ORDER = "notes_sort_order"
        private const val APP_THEME_MODE = "app_theme_mode"
    }

    suspend fun getNotesViewMode(): NotesViewMode {
        return NotesViewMode.fromName(keyValueStorage.getString(NOTES_VIEW_MODE).firstOrNull())
    }

    suspend fun setNotesViewMode(notesViewMode: NotesViewMode) {
        keyValueStorage.put(NOTES_VIEW_MODE, notesViewMode.name)
    }

    suspend fun getNotesSortOrder(): NotesSortOrder {
        return NotesSortOrder.fromName(keyValueStorage.getString(NOTES_SORT_ORDER).firstOrNull())
    }

    suspend fun setNotesSortOrder(notesSortOrder: NotesSortOrder) {
        keyValueStorage.put(NOTES_SORT_ORDER, notesSortOrder.name)
    }

    suspend fun getAppThemeMode(): AppThemeMode {
        return AppThemeMode.fromName(keyValueStorage.getString(APP_THEME_MODE).firstOrNull())
    }

    fun observeAppThemeMode(): Flow<AppThemeMode> {
        return keyValueStorage.getString(APP_THEME_MODE)
            .map { AppThemeMode.fromName(it) }
            .distinctUntilChanged()
    }

    suspend fun setAppThemeMode(appThemeMode: AppThemeMode) {
        keyValueStorage.put(APP_THEME_MODE, appThemeMode.name)
    }
}
