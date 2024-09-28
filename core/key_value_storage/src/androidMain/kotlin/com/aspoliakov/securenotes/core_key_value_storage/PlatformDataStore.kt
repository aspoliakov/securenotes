package com.aspoliakov.securenotes.core_key_value_storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Project SecureNotes
 */

internal class PlatformDataStore(private val context: Context) {
    fun getDataStore(): DataStore<Preferences> = getDataStore(
            producePath = { context.filesDir.resolve("datastore/$dataStoreFileName").absolutePath },
    )
}
