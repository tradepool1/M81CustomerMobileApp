package com.mentorhomeloans.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mentorhomeloans.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object interface targeting transactions history table.
 */
@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE loanAccountId = :loanAccountId ORDER BY transactionDate DESC")
    fun getTransactions(loanAccountId: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions WHERE loanAccountId = :loanAccountId")
    suspend fun clearTransactions(loanAccountId: String)
}
