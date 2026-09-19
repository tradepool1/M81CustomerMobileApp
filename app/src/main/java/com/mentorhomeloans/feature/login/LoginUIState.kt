package com.mentorhomeloans.feature.login

/**
 * UI State definition for the Login and OTP Verification flows.
 */
sealed interface LoginUIState {
    object Initial : LoginUIState
    object CaptchaLoading : LoginUIState
    data class CaptchaSuccess(val token: String) : LoginUIState
    object LoginLoading : LoginUIState
    data class Success(val customerId: String) : LoginUIState
    data class Error(val message: String) : LoginUIState
    object UpdatePassword : LoginUIState
}
