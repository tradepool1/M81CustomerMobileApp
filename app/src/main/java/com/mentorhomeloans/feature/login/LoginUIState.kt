package com.mentorhomeloans.feature.login

/**
 * UI State definition for the Login and OTP Verification flows.
 */
sealed interface LoginUIState {
    object EnterMobile : LoginUIState
    data class VerifyOtp(val sessionId: String) : LoginUIState
    object Loading : LoginUIState
    data class Success(val customerId: String) : LoginUIState
    data class Error(val message: String) : LoginUIState
}
