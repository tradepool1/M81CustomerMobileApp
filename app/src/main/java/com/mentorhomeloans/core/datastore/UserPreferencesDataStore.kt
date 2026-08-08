package com.mentorhomeloans.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.mentorhomeloans.core.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mentor_user_prefs")

/**
 * Handles typed settings saving and retrieving using AndroidX DataStore.
 */
@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeKey = booleanPreferencesKey(Constants.PREF_KEY_THEME)
    private val onboardingKey = booleanPreferencesKey(Constants.PREF_KEY_ONBOARDING)
    private val notificationKey = booleanPreferencesKey(Constants.PREF_KEY_NOTIFICATIONS)
    private val isLoggedInKey = booleanPreferencesKey(Constants.PREF_KEY_IS_LOGGED_IN)

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            isDarkModeEnabled = if (preferences.contains(themeKey)) preferences[themeKey] else null,
            areNotificationsEnabled = preferences[notificationKey] ?: true,
            isOnboardingCompleted = preferences[onboardingKey] ?: false,
            isLoggedIn = preferences[isLoggedInKey] ?: false
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = enabled
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[onboardingKey] = completed
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[notificationKey] = enabled
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isLoggedInKey] = isLoggedIn
        }
    }
}
