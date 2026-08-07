package com.mentorhomeloans.feature.notifications

import com.mentorhomeloans.domain.model.Notification

/**
 * UI State definition for Notifications list.
 */
sealed interface NotificationsUIState {
    object Loading : NotificationsUIState
    data class Success(val notifications: List<Notification>) : NotificationsUIState
    data class Error(val message: String) : NotificationsUIState
}
