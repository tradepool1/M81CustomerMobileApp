package com.mentorhomeloans.core.security.captcha

import android.app.Application
import android.content.Context
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import com.google.android.recaptcha.RecaptchaException
import com.mentorhomeloans.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production implementation of [CaptchaManager] using Google reCAPTCHA Enterprise SDK.
 *
 * Enforces:
 * - Dynamic and verified package name from Application context.
 * - Explicit action configuration (e.g. "LOGIN").
 * - Fresh, non-cached token generation for every attempt.
 * - Zero logging of full token strings in production.
 */
@Singleton
class RecaptchaEnterpriseManager @Inject constructor(
    @ApplicationContext private val context: Context
) : CaptchaManager {

    private val mutex = Mutex()
    private var recaptchaClient: RecaptchaClient? = null

    override val currentPackageName: String
        get() = context.packageName

    private val siteKey: String
        get() = try {
            context.getString(R.string.recaptcha_site_key)
        } catch (e: Exception) {
            ""
        }

    private var lastInitError: Throwable? = null

    /**
     * Initializes the reCAPTCHA client if not already cached.
     */
    override suspend fun initialize(): Boolean {
        return getOrInitClient() != null
    }

    private suspend fun getOrInitClient(): RecaptchaClient? {
        recaptchaClient?.let { return it }

        return mutex.withLock {
            recaptchaClient?.let { return it }

            val key = siteKey.trim()
            if (key.isBlank()) {
                val err = IllegalStateException("reCAPTCHA site key is missing or blank in strings.xml")
                lastInitError = err
                Timber.e(err, "reCAPTCHA configuration error")
                return null
            }

            val app = context.applicationContext as? Application
            if (app == null) {
                val err = IllegalStateException("Unable to resolve Application context for reCAPTCHA initialization")
                lastInitError = err
                Timber.e(err, "reCAPTCHA context error")
                return null
            }

            try {
                Timber.d("Initializing reCAPTCHA client (package=%s, key=%s...)", currentPackageName, key.take(8))
                var result = Recaptcha.getClient(app, key, INIT_TIMEOUT_MS)
                
                // If first attempt hits a Network Error, retry once after a brief backoff
                if (result.isFailure && result.exceptionOrNull() is RecaptchaException) {
                    val ex = result.exceptionOrNull() as RecaptchaException
                    if (ex.message?.contains("Network", ignoreCase = true) == true) {
                        Timber.w("reCAPTCHA initial attempt network error, retrying in 1.5s...")
                        kotlinx.coroutines.delay(1500L)
                        result = Recaptcha.getClient(app, key, INIT_TIMEOUT_MS)
                    }
                }

                result.fold(
                    onSuccess = { client ->
                        recaptchaClient = client
                        lastInitError = null
                        Timber.d("reCAPTCHA client successfully initialized")
                    },
                    onFailure = { error ->
                        lastInitError = error
                        Timber.e(error, "reCAPTCHA client initialization returned failure: %s", error.message)
                    }
                )
            } catch (e: Exception) {
                lastInitError = e
                Timber.e(e, "reCAPTCHA client initialization threw exception: %s", e.message)
            }
            recaptchaClient
        }
    }

    /**
     * Generates a fresh CAPTCHA token for the given action.
     */
    override suspend fun generateToken(action: String): CaptchaResult {
        // Validate site key
        if (siteKey.isBlank()) {
            Timber.e("Validation error: reCAPTCHA site key is blank")
            return CaptchaResult.Failure(CaptchaError.InvalidSiteKey)
        }

        // Validate package name
        if (currentPackageName.isBlank()) {
            Timber.e("Validation error: application package name is blank")
            return CaptchaResult.Failure(CaptchaError.InvalidPackageName)
        }

        // Initialize or get client
        val client = getOrInitClient()
        if (client == null) {
            val error = lastInitError
            Timber.e(error, "Initialization error: reCAPTCHA client could not be created: %s", error?.message)
            return if (error != null) {
                mapThrowableToCaptchaResult(error)
            } else {
                CaptchaResult.Failure(CaptchaError.InitializationFailed)
            }
        }

        return try {
            Timber.d("Executing reCAPTCHA challenge with action=%s, package=%s", action, currentPackageName)
            val customAction = RecaptchaAction.custom(action)
            val result = client.execute(customAction)

            result.fold(
                onSuccess = { token ->
                    if (token.isBlank()) {
                        Timber.e("reCAPTCHA returned an empty token")
                        CaptchaResult.Failure(CaptchaError.EmptyToken)
                    } else {
                        // Safe logging: never log full tokens
                        val masked = if (token.length > 8) "${token.take(4)}...${token.takeLast(4)}" else "***"
                        Timber.d("reCAPTCHA token generated successfully (action=%s, token=%s)", action, masked)
                        CaptchaResult.Success(token = token, action = action)
                    }
                },
                onFailure = { error ->
                    Timber.e(error, "reCAPTCHA execution returned failure")
                    mapThrowableToCaptchaResult(error)
                }
            )
        } catch (e: Exception) {
            Timber.e(e, "Exception during reCAPTCHA execution")
            mapThrowableToCaptchaResult(e)
        }
    }

    private fun mapThrowableToCaptchaResult(error: Throwable): CaptchaResult {
        return when (error) {
            is IOException -> CaptchaResult.Failure(CaptchaError.NetworkError, error)
            is RecaptchaException -> {
                if (error.message?.contains("Network", ignoreCase = true) == true ||
                    error.errorCode.name.contains("NETWORK", ignoreCase = true)
                ) {
                    CaptchaResult.Failure(CaptchaError.NetworkError, error)
                } else {
                    CaptchaResult.Failure(
                        CaptchaError.GenerationFailed("${error.errorCode}: ${error.errorMessage}"),
                        error
                    )
                }
            }
            else -> CaptchaResult.Failure(
                CaptchaError.GenerationFailed(error.message ?: "Unknown security verification failure"),
                error
            )
        }
    }

    companion object {
        private const val INIT_TIMEOUT_MS = 30000L
    }
}
