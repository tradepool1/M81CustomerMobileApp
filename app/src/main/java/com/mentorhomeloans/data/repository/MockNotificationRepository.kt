package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.local.dao.NotificationDao
import com.mentorhomeloans.data.mapper.NotificationMapper
import com.mentorhomeloans.domain.model.Notification
import com.mentorhomeloans.domain.model.NotificationIconType
import com.mentorhomeloans.domain.model.NotificationType
import com.mentorhomeloans.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of NotificationRepository.
 */
@Singleton
class MockNotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    private val mockNotifications = listOf(
        Notification(
            id = "n1",
            title = "EMI Auto-Debit Success",
            body = "Your monthly EMI of Rs. 39,500 for loan account ML202688019 was successfully paid on 05 Jul.",
            type = NotificationType.PAYMENT_CONFIRMATION,
            isRead = false,
            receivedAt = "2026-07-05T09:30:00Z",
            actionUrl = "mentor://transactions",
            iconType = NotificationIconType.PAYMENT
        ),
        Notification(
            id = "n2",
            title = "Q1 Statement Available",
            body = "Statement of account for the period Apr 2026 - Jun 2026 is now ready for view and download.",
            type = NotificationType.STATEMENT_AVAILABLE,
            isRead = false,
            receivedAt = "2026-07-01T10:00:00Z",
            actionUrl = "mentor://statements",
            iconType = NotificationIconType.DOCUMENT
        ),
        Notification(
            id = "n3",
            title = "Ticket Status Update",
            body = "Your support ticket TKT-2026-04981 has been updated. Tap to view the response.",
            type = NotificationType.TICKET_UPDATE,
            isRead = true,
            receivedAt = "2026-07-07T14:22:00Z",
            actionUrl = "mentor://support",
            iconType = NotificationIconType.SUPPORT
        )
    )

    override fun getNotifications(customerId: String): Flow<Result<List<Notification>>> = flow {
        emit(Result.Loading)
        notificationDao.insertNotifications(mockNotifications.map { NotificationMapper.toEntity(it, customerId) })

        notificationDao.getNotifications(customerId).collect { entities ->
            emit(Result.Success(entities.map { NotificationMapper.toDomain(it) }))
        }
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        notificationDao.markAsRead(notificationId)
        return Result.Success(Unit)
    }

    override suspend fun markAllAsRead(customerId: String): Result<Int> {
        notificationDao.markAllAsRead(customerId)
        return Result.Success(mockNotifications.size)
    }

    override fun getUnreadCount(customerId: String): Flow<Int> {
        return notificationDao.getUnreadCount(customerId)
    }
}
