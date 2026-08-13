package com.mentorhomeloans.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mentorhomeloans.data.local.dao.LoanDao
import com.mentorhomeloans.data.local.dao.NotificationDao
import com.mentorhomeloans.data.local.dao.TransactionDao
import com.mentorhomeloans.data.local.entity.LoanAccountEntity
import com.mentorhomeloans.data.local.entity.NotificationEntity
import com.mentorhomeloans.data.local.entity.TransactionEntity

/**
 * Main application Room database.
 */
@Database(
    entities = [
        LoanAccountEntity::class,
        TransactionEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loanDao(): LoanDao
    abstract fun transactionDao(): TransactionDao
    abstract fun notificationDao(): NotificationDao
}
