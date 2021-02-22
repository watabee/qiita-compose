package com.github.watabee.qiitacompose.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.core.remove
import androidx.datastore.preferences.createDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface UserDataStore {
    val accessTokenFlow: Flow<String?>

    suspend fun updateAccessToken(accessToken: String?)
}

@Singleton
internal class UserDataStoreImpl @Inject constructor(@ApplicationContext appContext: Context) : UserDataStore {
    private val dataStore: DataStore<Preferences> = appContext.createDataStore("user")

    private object PreferencesKeys {
        val ACCESS_TOKEN = preferencesKey<String>("access_token")
    }

    override val accessTokenFlow: Flow<String?> = dataStore.data
        .catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }
        .map { preferences -> preferences[PreferencesKeys.ACCESS_TOKEN] }
        .distinctUntilChanged()

    override suspend fun updateAccessToken(accessToken: String?) {
        dataStore.edit { preferences ->
            if (accessToken != null) {
                preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            } else {
                preferences.remove(PreferencesKeys.ACCESS_TOKEN)
            }
        }
    }
}
