package com.aspoliakov.securenotes.domain_user_state

import com.aspoliakov.securenotes.core_base.util.IOScope
import com.aspoliakov.securenotes.core_key_value_storage.KeyValueStorage
import com.aspoliakov.securenotes.domain_user_state.model.*
import com.aspoliakov.securenotes.domain_user_state.network.UserSettingsApiProvider
import io.github.aakira.napier.Napier
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Project SecureNotes
 */

class UserPrefsInteractor(
        private val keyValueStorage: KeyValueStorage,
        private val userStateInteractor: UserStateInteractor,
        private val userSettingsApiProvider: UserSettingsApiProvider,
) {

    companion object {
        private const val HANDLE_CHANGES_DELAY = 2000L

        private const val NOTES_VIEW_MODE = "notes_view_mode"
        private const val NOTES_SORT_ORDER = "notes_sort_order"
        private const val APP_THEME_MODE = "app_theme_mode"

        private const val SYNC_REQUIRED_SUFFIX = "_sync_required"
    }

    private val syncedPrefsMutex = Mutex()

    private var uploadJob: Job? = null

    suspend fun getNotesViewMode(): NotesViewMode {
        return NotesViewMode.fromName(keyValueStorage.getString(NOTES_VIEW_MODE).firstOrNull())
    }

    fun observeNotesViewMode(): Flow<NotesViewMode> {
        return keyValueStorage.getString(NOTES_VIEW_MODE)
            .map { NotesViewMode.fromName(it) }
            .distinctUntilChanged()
    }

    suspend fun setNotesViewMode(notesViewMode: NotesViewMode) {
        putSynced(NOTES_VIEW_MODE, notesViewMode.name)
    }

    suspend fun getNotesSortOrder(): NotesSortOrder {
        return NotesSortOrder.fromName(keyValueStorage.getString(NOTES_SORT_ORDER).firstOrNull())
    }

    fun observeNotesSortOrder(): Flow<NotesSortOrder> {
        return keyValueStorage.getString(NOTES_SORT_ORDER)
            .map { NotesSortOrder.fromName(it) }
            .distinctUntilChanged()
    }

    suspend fun setNotesSortOrder(notesSortOrder: NotesSortOrder) {
        putSynced(NOTES_SORT_ORDER, notesSortOrder.name)
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
        putSynced(APP_THEME_MODE, appThemeMode.name)
    }

    fun sync() = IOScope().launch {
        runCatching {
            uploadChanges()
            downloadChanges()
        }
            .onFailure {
                Napier.e("Error syncing user settings: $it")
            }
    }

    private suspend fun putSynced(key: String, value: String) {
        syncedPrefsMutex.withLock {
            keyValueStorage.put(key, value)
            keyValueStorage.put(key + SYNC_REQUIRED_SUFFIX, true)
        }
        scheduleUpload()
    }

    private fun scheduleUpload() {
        uploadJob?.cancel()
        uploadJob = IOScope().launch {
            delay(HANDLE_CHANGES_DELAY.milliseconds)
            runCatching { uploadChanges() }
                .onFailure {
                    Napier.e("Error uploading user settings: $it")
                }
        }
    }

    private suspend fun uploadChanges() {
        val token = userStateInteractor.getUserToken() ?: return
        val pendingSettings = listOfNotNull(
                pendingSetting(NOTES_VIEW_MODE) { getNotesViewMode().name },
                pendingSetting(NOTES_SORT_ORDER) { getNotesSortOrder().name },
                pendingSetting(APP_THEME_MODE) { getAppThemeMode().name },
        )
        if (pendingSettings.isEmpty()) return
        Napier.d("upload user settings to server: ${pendingSettings.map { it.key }}")
        userSettingsApiProvider.provideApi().saveSettings(
                token = token,
                request = PostUserSettingsRequest(
                        settings = pendingSettings.map { PostUserSettingItem(it.key, it.value) },
                ),
        )
        syncedPrefsMutex.withLock {
            pendingSettings.forEach {
                if (it.readCurrentValue() == it.value) {
                    keyValueStorage.put(it.key + SYNC_REQUIRED_SUFFIX, false)
                }
            }
        }
    }

    private suspend fun downloadChanges() {
        val token = userStateInteractor.getUserToken() ?: return
        val serverSettings = userSettingsApiProvider.provideApi().getAllSettings(token = token).settings
        syncedPrefsMutex.withLock {
            serverSettings.forEach { setting ->
                if (isSyncRequired(setting.key)) return@forEach
                when (setting.key) {
                    NOTES_VIEW_MODE -> NotesViewMode.entries
                        .firstOrNull { it.name == setting.value }
                        ?.let { keyValueStorage.put(NOTES_VIEW_MODE, it.name) }
                    NOTES_SORT_ORDER -> NotesSortOrder.entries
                        .firstOrNull { it.name == setting.value }
                        ?.let { keyValueStorage.put(NOTES_SORT_ORDER, it.name) }
                    APP_THEME_MODE -> AppThemeMode.entries
                        .firstOrNull { it.name == setting.value }
                        ?.let { keyValueStorage.put(APP_THEME_MODE, it.name) }
                    else -> Unit
                }
            }
        }
    }

    private suspend fun pendingSetting(
            key: String,
            readCurrentValue: suspend () -> String,
    ): PendingSetting? {
        if (!isSyncRequired(key)) return null
        return PendingSetting(
                key = key,
                value = readCurrentValue(),
                readCurrentValue = readCurrentValue,
        )
    }

    private suspend fun isSyncRequired(key: String): Boolean {
        return keyValueStorage.getBoolean(key + SYNC_REQUIRED_SUFFIX).firstOrNull() == true
    }

    private data class PendingSetting(
            val key: String,
            val value: String,
            val readCurrentValue: suspend () -> String,
    )
}
