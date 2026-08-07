package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Notification
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for in-app and push notification operations.
 */
interface NotificationRepository {

    /**
     * Observes all notifications for the logged-in customer.
     * Emits locally cached notifications immediately, then updates from network.
     *
     * @param customerId The customer identifier.
     * @return A [Flow] of [Result]<List<[Notification]>>.
     */
    fun getNotifications(customerId: String): Flow<Result<List<Notification>>>

    /**
     * Marks a specific notification as read.
     *
     * @param notificationId The notification identifier.
     * @return [Result.Success] with Unit on success.
     */
    suspend fun markAsRead(notificationId: String): Result<Unit>

    /**
     * Marks all unread notifications as read in bulk.
     *
     * @param customerId The customer identifier.
     * @return [Result.Success] with the count of notifications marked.
     */
    suspend fun markAllAsRead(customerId: String): Result<Int>

    /**
     * Returns the count of unread notifications as a [Flow].
     *
     * @param customerId The customer identifier.
     * @return A [Flow] of unread notification count.
     */
    fun getUnreadCount(customerId: String): Flow<Int>
}
