package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.LoanRepaymentDetail
import com.mentorhomeloans.domain.model.RepaymentScheduleItem
import com.mentorhomeloans.domain.model.RepaymentStatus
import com.mentorhomeloans.domain.model.RepaymentSummary
import com.mentorhomeloans.domain.repository.RepaymentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of RepaymentRepository.
 */
@Singleton
class MockRepaymentRepository @Inject constructor() : RepaymentRepository {

    private fun generateSchedule(): List<RepaymentScheduleItem> {
        val list = mutableListOf<RepaymentScheduleItem>()
        // Generate a few elements
        for (i in 1..240) {
            val status = when {
                i <= 24 -> RepaymentStatus.PAID
                i == 25 -> RepaymentStatus.UPCOMING
                else -> RepaymentStatus.PENDING
            }
            list.add(
                RepaymentScheduleItem(
                    installmentNumber = i,
                    dueDate = "2026-08-05", // Mock simplified date
                    emiAmount = 39500.0,
                    principalAmount = 12500.0,
                    interestAmount = 27000.0,
                    charges = 0.0,
                    openingBalance = 4500000.0 - (i - 1) * 12500,
                    closingBalance = 4500000.0 - i * 12500,
                    status = status,
                    paidDate = if (status == RepaymentStatus.PAID) "2026-07-05" else null,
                    paidAmount = if (status == RepaymentStatus.PAID) 39500.0 else null
                )
            )
        }
        return list
    }

    override fun getRepaymentSummary(loanAccountId: String): Flow<Result<RepaymentSummary>> = flow {
        emit(Result.Loading)
        delay(900)
        emit(
            Result.Success(
                RepaymentSummary(
                    loanAccountNumber = "ML202688019",
                    totalLoanAmount = 4500000.0,
                    totalAmountPaid = 948000.0,
                    totalAmountPending = 8532000.0, // simplified math
                    totalPrincipalPaid = 300000.0,
                    totalInterestPaid = 648000.0,
                    totalPrincipalPending = 3700000.0,
                    totalInterestPending = 4832000.0,
                    prepaymentAllowed = true,
                    prepaymentPenaltyPct = 0.0,
                    foreClosureAmount = 3715000.0,
                    schedule = generateSchedule()
                )
            )
        )
    }

    override suspend fun calculateForeclosure(
        loanAccountId: String,
        foreClosureDate: String
    ): Result<Double> {
        delay(800)
        return Result.Success(3710500.0)
    }

    override fun getLoanRepaymentDetails(loanId: String): Flow<Result<List<LoanRepaymentDetail>>> = flow {
        emit(Result.Loading)
        delay(500)
        emit(Result.Success(emptyList()))
    }
}

