package com.aspoliakov.securenotes.di

import com.aspoliakov.securenotes.core_db.di.databaseModule
import com.aspoliakov.securenotes.core_key_value_storage.di.dataStoreModule

/**
 * Project SecureNotes
 */

val dataModule = listOf(
        dataStoreModule,
        databaseModule,
)
