package com.dimi.advnotes.presentation.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val USER_PREFERENCES_NAME = "user_preferences"

const val LAYOUT_MODE_LINEAR = 1
const val LAYOUT_MODE_STAGGERED_GRID = 2

data class UserPreferences(
    val layoutMode: Int
)

class UserPreferencesRepository(context: Context) {

    private val dataStore: DataStore<Preferences> =
        context.createDataStore(
            name = USER_PREFERENCES_NAME
        )

    private object PreferencesKeys {
        val LAYOUT_MODE = preferencesKey<Int>("layout_span_count")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val layoutMode = preferences[PreferencesKeys.LAYOUT_MODE] ?: LAYOUT_MODE_STAGGERED_GRID
            UserPreferences(layoutMode)
        }

    suspend fun saveLayoutMode(layoutMode: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAYOUT_MODE] = layoutMode
        }
    }
}