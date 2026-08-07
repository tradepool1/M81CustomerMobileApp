package com.mentorhomeloans.core.network

import android.content.Context
import android.os.Build
import com.mentorhomeloans.core.security.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that appends the Authorization Bearer JWT token and device
 * information headers to all outgoing requests.
 *
 * Headers added:
 *  - Authorization  : Bearer <jwt>  (when a session is active)
 *  - Device-Name    : e.g. "generic_x86"
 *  - Device-Model   : e.g. "Android SDK built for x86"
 *  - Device-OS      : e.g. "Android 13"
 *  - Language       : e.g. "en"
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.getJwtToken()

        val requestBuilder = originalRequest.newBuilder()

        // ── Auth header ───────────────────────────────────────────────────────
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // ── Device info headers ───────────────────────────────────────────────
        requestBuilder.addHeader("Device-Name",  Build.DEVICE)
        requestBuilder.addHeader("Device-Model", Build.MODEL)
        requestBuilder.addHeader("Device-OS",    "Android ${Build.VERSION.RELEASE}")
        requestBuilder.addHeader("Language",     Locale.getDefault().language)

        return chain.proceed(requestBuilder.build())
    }
}
