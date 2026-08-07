package com.mentorhomeloans.data.mapper

import com.mentorhomeloans.data.local.entity.NotificationEntity
import com.mentorhomeloans.domain.model.Notification
import com.mentorhomeloans.domain.model.NotificationIconType
import com.mentorhomeloans.domain.model.NotificationType

/**
 * Maps notification entities.
 */
object NotificationMapper {

    fun toDomain(entity: NotificationEntity): Notification {
        return Notification(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            type = NotificationType.entries.find { it.name == entity.type } ?: NotificationType.GENERAL,
            isRead = entity.isRead,
            receivedAt = entity.receivedAt,
            actionUrl = entity.actionUrl,
            iconType = NotificationIconType.entries.find { it.name == entity.iconType } ?: NotificationIconType.INFO,
            metaData = emptyMap()
        )
    }

    fun toEntity(domain: Notification, customerId: String): NotificationEntity {
        return NotificationEntity(
            id = domain.id,
            customerId = customerId,
            title = domain.title,
            body = domain.body,
            type = domain.type.name,
            isRead = domain.isRead,
            receivedAt = domain.receivedAt,
            actionUrl = domain.actionUrl,
            iconType = domain.iconType.name
        )
    }
}
