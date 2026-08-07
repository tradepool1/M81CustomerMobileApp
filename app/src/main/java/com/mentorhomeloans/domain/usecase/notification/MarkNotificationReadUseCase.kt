package com.mentorhomeloans.domain.usecase.notification

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * Use case to mark a specific notification as read.
 *
 * @param notificationRepository Repository handling notifications.
 */
class MarkNotificationReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    /**
     * Executes marking notification as read.
     *
     * @param notificationId The notification identifier.
     * @return Result wrapping the outcome.
     */
    suspend operator fun invoke(notificationId: String): Result<Unit> {
        return notificationRepository.markAsRead(notificationId)
    }
}
