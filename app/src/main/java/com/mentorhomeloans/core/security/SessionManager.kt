package com.mentorhomeloans.core.security

import com.mentorhomeloans.core.common.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles JWT and refresh token storage and validation.
 *
 * Exposes a reactive state flow of the session state.
 */
@Singleton
class SessionManager @Inject constructor(
    private val encryptedPrefs: EncryptedPrefsManager
) {
    private val _isSessionActive = MutableStateFlow(!getJwtToken().isNullOrBlank())
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    /**
     * Saves full session tokens to secure storage.
     */
    fun saveSession(jwtToken: String, refreshToken: String, customerId: String, mobileNumber: String) {
        encryptedPrefs.saveString(Constants.SECURE_KEY_JWT, jwtToken)
        encryptedPrefs.saveString(Constants.SECURE_KEY_REFRESH, refreshToken)
        encryptedPrefs.saveString(Constants.SECURE_KEY_CUSTOMER_ID, customerId)
        encryptedPrefs.saveString(Constants.SECURE_KEY_MOBILE, mobileNumber)
        _isSessionActive.value = true
    }

    /**
     * Saves a JWT token and mobile number from the real OTP_Verification API response.
     * Use this when the API does not return a refreshToken or customerId.
     */
    fun saveJwt(jwtToken: String, mobileNumber: String) {
        encryptedPrefs.saveString(Constants.SECURE_KEY_JWT, jwtToken)
        encryptedPrefs.saveString(Constants.SECURE_KEY_MOBILE, mobileNumber)
        _isSessionActive.value = true
    }

    /**
     * Retrieves the current JWT token.
     */
    fun getJwtToken(): String? {
        return encryptedPrefs.getString(Constants.SECURE_KEY_JWT)
    }

    /**
     * Retrieves the customer ID.
     */
    fun getCustomerId(): String? {
        return encryptedPrefs.getString(Constants.SECURE_KEY_CUSTOMER_ID)
    }

    /**
     * Retrieves the saved mobile number.
     */
    fun getMobileNumber(): String? {
        return encryptedPrefs.getString(Constants.SECURE_KEY_MOBILE)
    }

    /**
     * Clears session storage.
     */
    fun clearSession() {
        encryptedPrefs.clearAll()
        _isSessionActive.value = false
    }
}
