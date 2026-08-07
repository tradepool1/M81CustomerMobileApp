package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.RepaymentSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for loan repayment schedule and prepayment operations.
 */
interface RepaymentRepository {

    /**
     * Fetches the full amortization schedule and repayment summary for a loan account.
     *
     * @param loanAccountId The loan account identifier.
     * @return A [Flow] of [Result]<[RepaymentSummary]>.
     */
    fun getRepaymentSummary(loanAccountId: String): Flow<Result<RepaymentSummary>>

    /**
     * Calculates the foreclosure amount for a given date.
     *
     * @param loanAccountId    The loan account identifier.
     * @param foreClosureDate  The proposed foreclosure date (yyyy-MM-dd).
     * @return [Result.Success] with the foreclosure amount in INR.
     */
    suspend fun calculateForeclosure(loanAccountId: String, foreClosureDate: String): Result<Double>
}
