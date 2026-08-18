package com.mentorhomeloans.domain.usecase.statement

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.LoanSOADetail
import com.mentorhomeloans.domain.repository.StatementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching Statement of Account (SOA) transaction details for a given loan ID.
 *
 * @param statementRepository Data source repository for statement records.
 */
class GetCusLoanSOADetailsUseCase @Inject constructor(
    private val statementRepository: StatementRepository
) {
    /**
     * Executes the SOA detail fetch operation.
     *
     * @param loanId The numeric loan ID (e.g. "23212").
     * @return [Flow] of [Result] wrapping a list of [LoanSOADetail].
     */
    operator fun invoke(loanId: String): Flow<Result<List<LoanSOADetail>>> {
        return statementRepository.getCusLoanSOADetails(loanId)
    }
}
