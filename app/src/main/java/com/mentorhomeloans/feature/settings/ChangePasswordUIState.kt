package com.mentorhomeloans.feature.settings

/**
 * UI State definition for the Change Password flow.
 */
sealed interface ChangePasswordUIState {
    object Initial : ChangePasswordUIState
    object Loading : ChangePasswordUIState
    data class Success(val message: String) : ChangePasswordUIState
    data class Error(val message: String) : ChangePasswordUIState
}
