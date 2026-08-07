package com.mentorhomeloans.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mentorhomeloans.data.local.entity.LoanAccountEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object interface targeting loan details transactions cache.
 */
@Dao
interface LoanDao {

    @Query("SELECT * FROM loan_accounts WHERE id = :id LIMIT 1")
    fun getLoanAccount(id: String): Flow<LoanAccountEntity?>

    @Query("SELECT * FROM loan_accounts")
    fun getAllLoanAccounts(): Flow<List<LoanAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanAccount(loanAccount: LoanAccountEntity)

    @Query("DELETE FROM loan_accounts WHERE id = :id")
    suspend fun deleteLoanAccount(id: String)
}
