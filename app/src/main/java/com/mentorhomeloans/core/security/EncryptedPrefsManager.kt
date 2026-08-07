package com.mentorhomeloans.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mentorhomeloans.core.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages access to EncryptedSharedPreferences for secure token/credential storage.
 *
 * Utilizes the Android Keystore to automatically encrypt keys and values.
 */
@Singleton
class EncryptedPrefsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey: MasterKey by lazy {
        try {
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        } catch (e: Exception) {
            Timber.e(e, "Error creating MasterKey, re-attempting fallback build")
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }
    }

    private val securePrefs: SharedPreferences by lazy {
        try {
            createEncryptedSharedPreferences()
        } catch (e: Exception) {
            Timber.e(e, "EncryptedSharedPreferences creation failed. Clearing corrupted prefs and retrying.")
            try {
                context.deleteSharedPreferences(Constants.SECURE_PREF_FILE)
                createEncryptedSharedPreferences()
            } catch (fallbackEx: Exception) {
                Timber.e(fallbackEx, "Fallback EncryptedSharedPreferences initialization failed. Using standard prefs as fallback.")
                context.getSharedPreferences("${Constants.SECURE_PREF_FILE}_fallback", Context.MODE_PRIVATE)
            }
        }
    }

    private fun createEncryptedSharedPreferences(): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            Constants.SECURE_PREF_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Saves a secure string value.
     */
    fun saveString(key: String, value: String?) {
        try {
            securePrefs.edit().putString(key, value).apply()
        } catch (e: Exception) {
            Timber.e(e, "Error saving string to securePrefs key: $key")
        }
    }

    /**
     * Retrieves a secure string value.
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        return try {
            securePrefs.getString(key, defaultValue)
        } catch (e: Exception) {
            Timber.e(e, "Error reading string from securePrefs key: $key")
            defaultValue
        }
    }

    /**
     * Clears all values inside secure storage.
     */
    fun clearAll() {
        try {
            securePrefs.edit().clear().apply()
        } catch (e: Exception) {
            Timber.e(e, "Error clearing securePrefs")
        }
    }
}

