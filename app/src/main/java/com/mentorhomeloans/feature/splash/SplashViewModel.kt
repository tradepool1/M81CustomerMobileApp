package com.mentorhomeloans.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mentorhomeloans.core.common.Constants
import com.mentorhomeloans.core.datastore.UserPreferencesDataStore
import com.mentorhomeloans.core.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for SplashScreen.
 *
 * Reads the actual persisted isLoggedIn flag and JWT token from storage
 * BEFORE deciding which screen to navigate to.
 * This avoids the race condition where navigation fires before DataStore emits.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesDataStore: UserPreferencesDataStore,
    private val sessionManager: SessionManager
) : ViewModel() {

    sealed class SplashDestination {
        object Loading : SplashDestination()
        object Dashboard : SplashDestination()
        object Login : SplashDestination()
        object Onboarding : SplashDestination()
    }

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            // Wait for splash duration
            delay(Constants.SPLASH_DURATION_MS)

            // Read the FIRST real value from DataStore (not the initial fallback)
            val prefs = preferencesDataStore.userPreferencesFlow.first()

            // Also check the JWT token directly from encrypted storage
            val hasToken = !sessionManager.getJwtToken().isNullOrBlank()

            _destination.value = when {
                prefs.isLoggedIn || hasToken -> SplashDestination.Dashboard
                prefs.isOnboardingCompleted  -> SplashDestination.Login
                else                         -> SplashDestination.Onboarding
            }
        }
    }
}
