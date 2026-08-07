package com.mentorhomeloans.domain.usecase.notification

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Notification
import com.mentorhomeloans.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe customer notifications.
 *
 * @param notificationRepository Repository handling notifications.
 */
class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    /**
     * Executes the notification flow retrieval.
     *
     * @param customerId The customer identifier.
     * @return Flow of notification list.
     */
    operator fun invoke(customerId: String): Flow<Result<List<Notification>>> {
        return notificationRepository.getNotifications(customerId)
    }
}
