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


    private val PIN_MODE = booleanPreferencesKey("pin_mode")
    private val TIME_CREATE = booleanPreferencesKey("time_create")
    private val SHARING = booleanPreferencesKey("sharing")
    private val BOTTOM = booleanPreferencesKey("bottom")


    val pinMode: Flow<Boolean> = settingsDataStore.data.map { preferences ->
        preferences[PIN_MODE] ?: false
    }
    val sortTimeCreated: Flow<Boolean> = settingsDataStore.data.map { preferences ->
        preferences[TIME_CREATE] ?: false
    }
    val sharing: Flow<Boolean> = settingsDataStore.data.map { preferences ->
        preferences[SHARING] ?: false
    }
    val showBottom: Flow<Boolean> = settingsDataStore.data.map { preferences ->
        preferences[BOTTOM] ?: false
    }


    suspend fun setPinMode(isSet: Boolean) {
        settingsDataStore.edit { settings ->
            settings[PIN_MODE] = isSet
        }
    }
    suspend fun setSortByTimeCreated(isSet: Boolean) {
        settingsDataStore.edit { settings ->
            settings[TIME_CREATE] = isSet
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

}