package com.cuinsolutions.macrosmanager.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class MacrosManagerPreferencesRepository @Inject constructor(@ApplicationContext context: Context,
                                                             val dataStore: DataStore<Preferences>
) {

    val macrosManagerPreferences = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        if (preferences.contains(MacrosManagerPreferencesKeys.currentDate) && preferences[MacrosManagerPreferencesKeys.currentDate] != null) {
            MacrosManagerPreferences(
                LocalDate.parse(preferences[MacrosManagerPreferencesKeys.currentDate])
                    ?: LocalDate.now()
            )
        } else {
            MacrosManagerPreferences(LocalDate.now())
        }
    }

    suspend fun updateCurrentDate() {
        dataStore.edit { preferences ->
            preferences.toMutablePreferences().apply {
                this[MacrosManagerPreferencesKeys.currentDate] = LocalDate.now().toString()
            }
        }
    }
}

private object MacrosManagerPreferencesKeys {
    val currentDate = stringPreferencesKey("current_date")
}