package com.mentorhomeloans.core.common

/**
 * Application-wide constants.
 */
object Constants {

    // ── API Configuration ─────────────────────────────────────────────────────
    const val API_CONNECT_TIMEOUT = 30L
    const val API_READ_TIMEOUT    = 30L
    const val API_WRITE_TIMEOUT   = 30L

    // ── OTP ───────────────────────────────────────────────────────────────────
    const val OTP_LENGTH = 6

    // ── Secure Storage Keys ───────────────────────────────────────────────────
    const val SECURE_PREF_FILE       = "mentor_secure_prefs"
    const val SECURE_KEY_JWT         = "jwt_token"
    const val SECURE_KEY_REFRESH     = "refresh_token"
    const val SECURE_KEY_CUSTOMER_ID = "customer_id"
    const val SECURE_KEY_MOBILE      = "mobile_number"

    // ── DataStore Preference Keys ─────────────────────────────────────────────
    const val PREF_KEY_THEME         = "is_dark_mode"
    const val PREF_KEY_ONBOARDING    = "is_onboarding_completed"
    const val PREF_KEY_NOTIFICATIONS = "notifications_enabled"

    // ── Database ──────────────────────────────────────────────────────────────
    const val DATABASE_NAME = "mentor_home_loans_db"

    // ── Splash ────────────────────────────────────────────────────────────────
    const val SPLASH_DURATION_MS = 2000L

    // ── Date Formats ─────────────────────────────────────────────────────────
    const val DATE_FORMAT_API     = "yyyy-MM-dd"
    const val DATE_FORMAT_DISPLAY = "dd MMM yyyy"
}
