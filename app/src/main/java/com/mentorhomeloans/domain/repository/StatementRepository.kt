package com.mentorhomeloans.domain.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Statement
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for loan statement operations.
 */
interface StatementRepository {

    /**
     * Fetches the list of available statements for a loan account.
     *
     * @param loanAccountId The loan account identifier.
     * @return A [Flow] of [Result]<List<[Statement]>>.
     */
    fun getStatements(loanAccountId: String): Flow<Result<List<Statement>>>

    /**
     * Initiates download of a statement PDF to the device's downloads directory.
     *
     * @param statement The [Statement] to download.
     * @return [Result.Success] with the local file path, [Result.Error] on failure.
     */
    suspend fun downloadStatementPdf(statement: Statement): Result<String>

    /**
     * Initiates download of a statement CSV to the device's downloads directory.
     *
     * @param statement The [Statement] to download.
     * @return [Result.Success] with the local file path, [Result.Error] on failure.
     */
    suspend fun downloadStatementCsv(statement: Statement): Result<String>
}
