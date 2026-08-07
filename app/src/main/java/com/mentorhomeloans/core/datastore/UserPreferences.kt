package com.mentorhomeloans.core.datastore

/**
 * Data class representing user customization preferences.
 *
 * @property isDarkModeEnabled Theme setting (null means follow system).
 * @property areNotificationsEnabled Push notification preference.
 * @property isOnboardingCompleted Status of onboarding screens.
 */
data class UserPreferences(
    val isDarkModeEnabled: Boolean?,
    val areNotificationsEnabled: Boolean,
    val isOnboardingCompleted: Boolean
)
