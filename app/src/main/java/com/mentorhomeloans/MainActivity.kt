package com.mentorhomeloans

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.mentorhomeloans.core.datastore.UserPreferences
import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import com.mentorhomeloans.core.navigation.AppNavGraph
import com.mentorhomeloans.core.ui.theme.MentorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Single Activity entry point for the MentorApp.
 *
 * Hosts the Jetpack Compose UI, provides edge-to-edge rendering,
 * and bootstraps the Navigation Graph with theme context.
 *
 * Session / login detection is fully handled inside [SplashViewModel]
 * to avoid the race condition where initial = false would fire
 * navigation before DataStore emitted the persisted value.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesDataStore: UserPreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val prefs by preferencesDataStore.userPreferencesFlow.collectAsState(
                initial = UserPreferences(
                    isDarkModeEnabled = null,
                    areNotificationsEnabled = true,
                    isOnboardingCompleted = false,
                    isLoggedIn = false
                )
            )

            // Honour user's explicit theme preference; fall back to system default
            val isDark = prefs.isDarkModeEnabled ?: isSystemInDarkTheme()

            MentorTheme(darkTheme = isDark, dynamicColor = true) {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    preferencesDataStore = preferencesDataStore
                )
            }
        }
    }
}