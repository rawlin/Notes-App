package com.rawlin.notesapp.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.rawlin.notesapp.utils.Constants.PREFERENCE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(PREFERENCE_NAME)

class DataStoreManager @Inject constructor(
    @ApplicationContext appContext: Context
) {
    private val settingsDataStore = appContext.dataStore


    private val PIN_MODE = booleanPreferencesKey(PIN_MODE_KEY)
    private val SHARING = booleanPreferencesKey(SHARING_KEY)
    private val BOTTOM = booleanPreferencesKey(BOTTOM_KEY)
    private val FIRST_APP_OPEN = booleanPreferencesKey(FIRST_APP_OPEN_KEY)


    val pinMode: Flow<Boolean> = settingsDataStore.data.map { preferences ->
        preferences[PIN_MODE] ?: true
    }
    val sharing: Flow<Boolean> = settingsDataStore.data.map { preferences ->
        preferences[SHARING] ?: true
    }
    val showBottom: Flow<Boolean> = settingsDataStore.data.map { preferences ->
        preferences[BOTTOM] ?: false
    }
    val firstAppOpen: Flow<Boolean> = settingsDataStore.data.map { preferences ->
        preferences[FIRST_APP_OPEN] ?: true
    }


    suspend fun setPinMode(isSet: Boolean) {
        settingsDataStore.edit { settings ->
            settings[PIN_MODE] = isSet
        }
    }

    suspend fun setSharing(isSet: Boolean) {
        settingsDataStore.edit { settings ->
            settings[SHARING] = isSet
        }
    }
    suspend fun setShowBottom(isSet: Boolean) {
        settingsDataStore.edit { settings ->
            settings[BOTTOM] = isSet
        }
    }

    suspend fun setFirstAppOpenFalse() {
        settingsDataStore.edit { settings ->
            settings[FIRST_APP_OPEN] = false
        }
    }

    companion object {
        private const val PIN_MODE_KEY = "pin_mode"
        private const val SHARING_KEY = "sharing"
        private const val BOTTOM_KEY = "bottom"
        private const val FIRST_APP_OPEN_KEY = "first_app_open"

    }

}