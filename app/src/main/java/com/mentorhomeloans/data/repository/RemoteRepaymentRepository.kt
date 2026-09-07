package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.local.dao.LoanDao
import com.mentorhomeloans.data.remote.api.RepaymentApiService
import com.mentorhomeloans.domain.model.LoanRepaymentDetail
import com.mentorhomeloans.domain.model.RepaymentSummary
import com.mentorhomeloans.domain.repository.RepaymentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote implementation of [RepaymentRepository].
 * Calls the GetLoanRepaymentDetails API endpoint and maps the flat
 * DTO list into the [LoanRepaymentDetail] domain model.
 */
@Singleton
class RemoteRepaymentRepository @Inject constructor(
    private val repaymentApiService: RepaymentApiService,
    private val loanDao: LoanDao
) : RepaymentRepository {

    /**
     * Legacy – not used by the Repayment screen but kept to satisfy the interface.
     */
    override fun getRepaymentSummary(loanAccountId: String): Flow<Result<RepaymentSummary>> = flow {
        emit(Result.Error(Exception("Not implemented – use getLoanRepaymentDetails")))
    }

    /**
     * Legacy – not implemented remotely yet.
     */
    override suspend fun calculateForeclosure(
        loanAccountId: String,
        foreClosureDate: String
    ): Result<Double> = Result.Error(Exception("Not implemented"))

    /**
     * Calls POST /GetLoanRepaymentDetails?loanId={loanId} and maps the
     * response list to a list of [LoanRepaymentDetail].
     */
    override fun getLoanRepaymentDetails(loanId: String): Flow<Result<List<LoanRepaymentDetail>>> = flow {
        emit(Result.Loading)
        try {
            var effectiveLoanId = loanId
            // Resolve numeric loanId from accountNumber if needed
            if (loanId.toLongOrNull() == null) {
                val cached = loanDao.getLoanAccountByAccountNumber(loanId).firstOrNull()
                if (cached != null && cached.id.toLongOrNull() != null) {
                    effectiveLoanId = cached.id
                }
            }

            val dtoList = repaymentApiService.getLoanRepaymentDetails(effectiveLoanId)
            val domainList = dtoList.map { dto ->
                LoanRepaymentDetail(
                    period = dto.period,
                    emiAmount = dto.emiAmount,
                    emiDueDate = dto.emiDueDate,
                    emiStatus = dto.emiPaidOD,
                    totalPrinciplePaid = dto.totalPrinciplePaid
                )
            }
            emit(Result.Success(domainList))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emit(Result.Error(e))
        }
    }
}
