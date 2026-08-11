package com.mentorhomeloans.core.network

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.mentorhomeloans.BuildConfig
import com.mentorhomeloans.core.security.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that appends the Authorization Bearer JWT token and device
 * information headers to all outgoing requests.
 *
 * Headers added:
 *  - Authorization  : Bearer <jwt>         (when a session is active)
 *  - X-Device-Id    : Android ID (unique per device install)
 *  - X-Device-Name  : e.g. "Pixel 6 Pro"
 *  - X-OS-Version   : e.g. "13"
 *  - X-Platform     : "android" (static)
 *  - X-App-Version  : e.g. "1.0.0" (from BuildConfig)
 *  - X-Device-IMEI  : IMEI if READ_PHONE_STATE is granted, else ANDROID_ID
 *  - X-Device-Model : e.g. "Pixel 6 Pro"
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : Interceptor {

    @SuppressLint("HardwareIds", "MissingPermission")
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.getJwtToken()

        val requestBuilder = originalRequest.newBuilder()

        // ── Auth header ───────────────────────────────────────────────────────
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // ── Device ID (Android ID — stable per device/user) ───────────────────
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"

        // ── IMEI (requires READ_PHONE_STATE permission on API ≥ 26) ──────────
        val imei: String = runCatching {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tm.imei ?: androidId
            } else {
                @Suppress("DEPRECATION")
                tm.deviceId ?: androidId
            }
        }.getOrDefault(androidId)   // fallback to ANDROID_ID if permission denied

        // ── Device info headers (matching server-expected names) ──────────────
        requestBuilder
            .addHeader("X-Device-Id",    androidId)
            .addHeader("X-Device-Name",  "${Build.MANUFACTURER} ${Build.MODEL}".trim())
            .addHeader("X-OS-Version",   Build.VERSION.RELEASE)
            .addHeader("X-Platform",     "android")
            .addHeader("X-App-Version",  "1.0.0")
            .addHeader("X-Device-IMEI",  imei)
            .addHeader("X-Device-Model", Build.MODEL)

        return chain.proceed(requestBuilder.build())
    }
}
