package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.local.dao.LoanDao
import com.mentorhomeloans.data.mapper.LoanMapper
import com.mentorhomeloans.domain.model.LoanAccount
import com.mentorhomeloans.domain.model.LoanStatus
import com.mentorhomeloans.domain.model.LoanType
import com.mentorhomeloans.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock implementation of LoanRepository.
 */
@Singleton
class MockLoanRepository @Inject constructor(
    private val loanDao: LoanDao
) : LoanRepository {

    private val mockLoan1 = LoanAccount(
        id = "24559",
        accountNumber = "ML202688019",
        loanType = LoanType.HOME_LOAN,
        sanctionAmount = 4500000.0,
        disbursedAmount = 4000000.0,
        outstandingAmount = 3750000.0,
        interestRate = 8.65,
        tenure = 240,
        remainingTenure = 216,
        emiAmount = 39500.0,
        nextEmiDate = "2026-08-05",
        nextEmiAmount = 39500.0,
        isOverdue = false,
        overdueAmount = 0.0,
        overdueEmiCount = 0,
        startDate = "2024-08-05",
        maturityDate = "2044-08-05",
        paidEmiCount = 24,
        totalEmiCount = 240,
        status = LoanStatus.ACTIVE,
        branchName = "Mumbai Corporate Branch",
        loanManagerName = "Ananya Sharma"
    )

    private val mockLoan2 = LoanAccount(
        id = "loan_99121",
        accountNumber = "LAP202599100",
        loanType = LoanType.LAP,
        sanctionAmount = 1500000.0,
        disbursedAmount = 1500000.0,
        outstandingAmount = 1420000.0,
        interestRate = 10.50,
        tenure = 120,
        remainingTenure = 105,
        emiAmount = 20240.0,
        nextEmiDate = "2026-08-10",
        nextEmiAmount = 20240.0,
        isOverdue = false,
        overdueAmount = 0.0,
        overdueEmiCount = 0,
        startDate = "2025-03-10",
        maturityDate = "2035-03-10",
        paidEmiCount = 15,
        totalEmiCount = 120,
        status = LoanStatus.ACTIVE,
        branchName = "Pune Branch",
        loanManagerName = "Rahul Verma"
    )

    override fun getLoanAccount(customerId: String): Flow<Result<LoanAccount>> = flow {
        emit(Result.Loading)
        loanDao.insertLoanAccount(LoanMapper.toEntity(mockLoan1))

        loanDao.getLoanAccount(mockLoan1.id).collect { entity ->
            if (entity != null) {
                emit(Result.Success(LoanMapper.toDomain(entity)))
            } else {
                emit(Result.Error(Exception("No account in cache")))
            }
        }
    }

    override fun getAllLoanAccounts(customerId: String): Flow<Result<List<LoanAccount>>> = flow {
        emit(Result.Loading)
        loanDao.insertLoanAccount(LoanMapper.toEntity(mockLoan1))
        loanDao.insertLoanAccount(LoanMapper.toEntity(mockLoan2))

        loanDao.getAllLoanAccounts().collect { entities ->
            if (entities.isNotEmpty()) {
                emit(Result.Success(entities.map { LoanMapper.toDomain(it) }))
            } else {
                emit(Result.Error(Exception("No accounts found")))
            }
        }
    }

    override fun getLoanById(loanId: String): Flow<Result<LoanAccount>> = flow {
        emit(Result.Loading)
        // Ensure they are in DB
        loanDao.insertLoanAccount(LoanMapper.toEntity(mockLoan1))
        loanDao.insertLoanAccount(LoanMapper.toEntity(mockLoan2))

        loanDao.getLoanAccount(loanId).collect { entity ->
            if (entity != null) {
                emit(Result.Success(LoanMapper.toDomain(entity)))
            } else {
                emit(Result.Error(Exception("No account in cache")))
            }
        }
    }

    override suspend fun refreshLoanAccount(customerId: String): Result<Unit> {
        // Simulate refresh success
        loanDao.insertLoanAccount(LoanMapper.toEntity(mockLoan1))
        return Result.Success(Unit)
    }
}
