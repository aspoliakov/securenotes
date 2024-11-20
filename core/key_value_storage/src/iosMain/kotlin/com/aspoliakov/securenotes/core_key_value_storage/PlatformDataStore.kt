package com.aspoliakov.securenotes.core_key_value_storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

/**
 * Project SecureNotes
 */

internal class PlatformDataStore {

    @OptIn(ExperimentalForeignApi::class)
    fun getDataStore(): DataStore<Preferences> = getDataStore(
            producePath = {
                val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                        directory = NSDocumentDirectory,
                        inDomain = NSUserDomainMask,
                        appropriateForURL = null,
                        create = false,
                        error = null,
                )
                requireNotNull(documentDirectory).path + "/$DATA_STORE_FILE_NAME"
            },
    )
}
