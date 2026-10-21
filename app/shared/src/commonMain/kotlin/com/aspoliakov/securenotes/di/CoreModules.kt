package com.aspoliakov.securenotes.di

import com.aspoliakov.securenotes.core_base.di.coreBaseModule
import com.aspoliakov.securenotes.core_db.di.coreDbModule
import com.aspoliakov.securenotes.core_key_value_storage.di.coreKeyValueStorageModule

/**
 * Project SecureNotes
 */

val coreModules = listOf(
        coreBaseModule,
        coreDbModule,
        coreKeyValueStorageModule,
)
