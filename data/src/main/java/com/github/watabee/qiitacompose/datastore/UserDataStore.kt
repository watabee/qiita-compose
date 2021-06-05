package com.github.watabee.qiitacompose.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.watabee.qiitacompose.data.UserData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface UserDataStore {
    val userDataFlow: Flow<UserData?>

    suspend fun updateUserData(accessToken: String, userImageUrl: String)

    suspend fun clear()
}

@Singleton
internal class UserDataStoreImpl @Inject constructor(@ApplicationContext private val appContext: Context) : UserDataStore {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user")

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val USER_IMAGE_URL = stringPreferencesKey("user_image_url")
    }

    override val userDataFlow: Flow<UserData?> = appContext.dataStore.data
        .catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }
        .map { preferences ->
            val accessToken = preferences[PreferencesKeys.ACCESS_TOKEN]
            if (accessToken != null) {
                UserData(accessToken = accessToken, imageUrl = preferences[PreferencesKeys.USER_IMAGE_URL])
            } else {
                null
            }
        }
        .distinctUntilChanged()

    override suspend fun updateUserData(accessToken: String, userImageUrl: String) {
        appContext.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferencesKeys.USER_IMAGE_URL] = userImageUrl
        }
    }

    override suspend fun clear() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
