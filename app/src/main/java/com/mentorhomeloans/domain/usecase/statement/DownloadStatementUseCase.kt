package com.mentorhomeloans.domain.usecase.statement

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Statement
import com.mentorhomeloans.domain.repository.StatementRepository
import javax.inject.Inject

/**
 * Use case for downloading a statement as PDF or CSV.
 *
 * @param statementRepository Data source for statement operations.
 */
class DownloadStatementUseCase @Inject constructor(
    private val statementRepository: StatementRepository
) {
    /**
     * Downloads the statement as a PDF file.
     *
     * @param statement The statement to download.
     * @return [Result.Success] with the local file path string.
     */
    suspend fun downloadPdf(statement: Statement): Result<String> {
        return statementRepository.downloadStatementPdf(statement)
    }

    /**
     * Downloads the statement as a CSV file.
     *
     * @param statement The statement to download.
     * @return [Result.Success] with the local file path string.
     */
    suspend fun downloadCsv(statement: Statement): Result<String> {
        return statementRepository.downloadStatementCsv(statement)
    }
}
