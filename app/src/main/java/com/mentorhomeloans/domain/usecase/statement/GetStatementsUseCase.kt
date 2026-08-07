package com.mentorhomeloans.domain.usecase.statement

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Statement
import com.mentorhomeloans.domain.repository.StatementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching available account statements.
 *
 * @param statementRepository Data source for statement records.
 */
class GetStatementsUseCase @Inject constructor(
    private val statementRepository: StatementRepository
) {
    /**
     * Returns a [Flow] of available statements for the given loan account.
     *
     * @param loanAccountId The loan account identifier.
     * @return [Flow]<[Result]<List<[Statement]>>>.
     */
    operator fun invoke(loanAccountId: String): Flow<Result<List<Statement>>> {
        return statementRepository.getStatements(loanAccountId)
    }
}
