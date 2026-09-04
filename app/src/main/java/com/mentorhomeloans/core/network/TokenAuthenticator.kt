package com.mentorhomeloans.core.network

import android.content.Context
import android.provider.Settings
import com.mentorhomeloans.core.security.SessionManager
import com.mentorhomeloans.data.remote.api.AuthApiService
import com.mentorhomeloans.data.remote.dto.RefreshTokenRequestDto
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Custom OkHttp Authenticator that handles 401 Unauthorized responses by
 * requesting a new access token using the stored refresh token.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    @Named("RefreshService") private val refreshService: AuthApiService,
    @ApplicationContext private val context: Context
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Only attempt refresh once per request to avoid infinite loops
        if (response.responseCount > 2) {
            Timber.w("Token refresh attempted multiple times. Giving up.")
            return null
        }

        val refreshToken = sessionManager.getRefreshToken()
        val mobileNumber = sessionManager.getMobileNumber()

        if (refreshToken.isNullOrBlank() || mobileNumber.isNullOrBlank()) {
            Timber.w("No refresh token or mobile number found. Cannot refresh.")
            return null
        }

        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"

        Timber.d("Access token expired. Attempting to refresh using: $refreshToken")

        synchronized(this) {
            val currentToken = sessionManager.getJwtToken()
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            // If another thread already refreshed the token, use the new one
            if (currentToken != requestToken && !currentToken.isNullOrBlank()) {
                Timber.d("Token was already refreshed by another thread. Retrying with new token.")
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // Perform refresh call synchronously
            try {
                val refreshCall = refreshService.refreshAccessToken(
                    RefreshTokenRequestDto(
                        refreshToken = refreshToken,
                        deviceId = androidId
                    )
                )

                val refreshResponse = refreshCall.execute()

                if (refreshResponse.isSuccessful && refreshResponse.body()?.status == true) {
                    val body = refreshResponse.body()!!
                    val newAccessToken = body.accessToken
                    val newRefreshToken = body.refreshToken

                    if (!newAccessToken.isNullOrBlank()) {
                        sessionManager.saveSession(
                            jwtToken = newAccessToken,
                            refreshToken = newRefreshToken ?: refreshToken,
                            mobileNumber = mobileNumber,
                            jwtExpiry = body.accessTokenExpiresAt,
                            refreshExpiry = body.refreshTokenExpiresAt
                        )

                        Timber.d("Successfully refreshed access token.")

                        return response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    }
                }

                Timber.e("Token refresh failed or returned invalid data: ${refreshResponse.code()}")
                // Clear session if refresh fails to force user to login again
                sessionManager.clearSession()
            } catch (e: Exception) {
                Timber.e(e, "Exception during token refresh")
            }
        }

        return null
    }

    private val Response.responseCount: Int
        get() {
            var count = 1
            var r = priorResponse
            while (r != null) {
                count++
                r = r.priorResponse
            }
            return count
        }
}
