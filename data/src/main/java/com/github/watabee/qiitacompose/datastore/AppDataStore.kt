package com.github.watabee.qiitacompose.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface AppDataStore {
    suspend fun lastTagsFetchedAt(): Long

    suspend fun updateLastTagsFetchedAt(fetchedAt: Long)
}

@Singleton
internal class AppDataStoreImpl @Inject constructor(@ApplicationContext private val appContext: Context) : AppDataStore {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app")

    private object PreferencesKeys {
        val LAST_TAGS_FETCHED_AT = longPreferencesKey("last_tags_fetched_at")
    }

    override suspend fun lastTagsFetchedAt(): Long {
        return appContext.dataStore.data
            .map { preferences -> preferences[PreferencesKeys.LAST_TAGS_FETCHED_AT] }
            .catch { emit(null) }
            .firstOrNull() ?: 0L
    }

    override suspend fun updateLastTagsFetchedAt(fetchedAt: Long) {
        appContext.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_TAGS_FETCHED_AT] = fetchedAt
        }
    }
}
