package com.mentorhomeloans.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mentorhomeloans.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object interface targeting notifications database table.
 */
@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE customerId = :customerId ORDER BY receivedAt DESC")
    fun getNotifications(customerId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE customerId = :customerId")
    suspend fun markAllAsRead(customerId: String)

    @Query("SELECT COUNT(*) FROM notifications WHERE customerId = :customerId AND isRead = 0")
    fun getUnreadCount(customerId: String): Flow<Int>

    @Query("DELETE FROM notifications WHERE customerId = :customerId")
    suspend fun clearNotifications(customerId: String)
}
