package com.aspoliakov.securenotes.core_key_value_storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Project SecureNotes
 */

class KeyValueStorage(private val dataStore: DataStore<Preferences>) {

    suspend fun put(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    suspend fun put(key: String, value: Long) {
        dataStore.edit { it[longPreferencesKey(key)] = value }
    }

    suspend fun put(key: String, value: Int) {
        dataStore.edit { it[intPreferencesKey(key)] = value }
    }

    fun getDataFlow(): Flow<Preferences> {
        return dataStore.data
    }

    fun getString(key: String): Flow<String?> {
        return dataStore.data.map { it[stringPreferencesKey(key)]?.ifEmpty { null } }
    }

    fun getLong(key: String): Flow<Long?> {
        return dataStore.data.map { it[longPreferencesKey(key)] }
    }

    fun getInt(key: String): Flow<Int?> {
        return dataStore.data.map { it[intPreferencesKey(key)] }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
