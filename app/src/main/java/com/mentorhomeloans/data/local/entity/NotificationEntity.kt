package com.mentorhomeloans.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing cached Notification messages.
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val title: String,
    val body: String,
    val type: String,
    val isRead: Boolean,
    val receivedAt: String,
    val actionUrl: String?,
    val iconType: String
)
