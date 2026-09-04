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
    fun saveSession(
        jwtToken: String,
        refreshToken: String,
        mobileNumber: String,
        jwtExpiry: String? = null,
        refreshExpiry: String? = null
    ) {
        encryptedPrefs.saveString(Constants.SECURE_KEY_JWT, jwtToken)
        encryptedPrefs.saveString(Constants.SECURE_KEY_REFRESH, refreshToken)
        encryptedPrefs.saveString(Constants.SECURE_KEY_MOBILE, mobileNumber)
        encryptedPrefs.saveString(Constants.SECURE_KEY_JWT_EXPIRY, jwtExpiry)
        encryptedPrefs.saveString(Constants.SECURE_KEY_REFRESH_EXPIRY, refreshExpiry)
        _isSessionActive.value = true
    }

    /**
     * Saves full session tokens including customerId to secure storage.
     */
    fun saveSession(
        jwtToken: String,
        refreshToken: String,
        customerId: String,
        mobileNumber: String,
        jwtExpiry: String? = null,
        refreshExpiry: String? = null
    ) {
        encryptedPrefs.saveString(Constants.SECURE_KEY_JWT, jwtToken)
        encryptedPrefs.saveString(Constants.SECURE_KEY_REFRESH, refreshToken)
        encryptedPrefs.saveString(Constants.SECURE_KEY_CUSTOMER_ID, customerId)
        encryptedPrefs.saveString(Constants.SECURE_KEY_MOBILE, mobileNumber)
        encryptedPrefs.saveString(Constants.SECURE_KEY_JWT_EXPIRY, jwtExpiry)
        encryptedPrefs.saveString(Constants.SECURE_KEY_REFRESH_EXPIRY, refreshExpiry)
        _isSessionActive.value = true
    }

    /**
     * Retrieves the current JWT token.
     */
    fun getJwtToken(): String? {
        return encryptedPrefs.getString(Constants.SECURE_KEY_JWT)
    }

    /**
     * Retrieves the current Refresh token.
     */
    fun getRefreshToken(): String? {
        return encryptedPrefs.getString(Constants.SECURE_KEY_REFRESH)
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
