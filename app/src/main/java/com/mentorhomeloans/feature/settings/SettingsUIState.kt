package com.mentorhomeloans.feature.settings

/**
 * State parameters for Settings view.
 */
sealed interface SettingsUIState {
    object Idle : SettingsUIState
    object Loading : SettingsUIState
    object LogoutSuccess : SettingsUIState
    data class Error(val message: String) : SettingsUIState
}
