package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.remote.api.StatementApiService
import com.mentorhomeloans.domain.model.LoanSOADetail
import com.mentorhomeloans.domain.model.Statement
import com.mentorhomeloans.domain.model.StatementType
import com.mentorhomeloans.domain.repository.StatementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote implementation of [StatementRepository] interacting with Retrofit API endpoints.
 */
@Singleton
class RemoteStatementRepository @Inject constructor(
    private val statementApiService: StatementApiService
) : StatementRepository {

    /**
     * Executes POST /GetCusLoanSOADetails?loanId={loanId} and maps response DTOs to [LoanSOADetail].
     * Handles all edge/corner cases including null values, dirty strings (e.g. \N), and missing fields.
     */
    override fun getCusLoanSOADetails(loanId: String): Flow<Result<List<LoanSOADetail>>> = flow {
        emit(Result.Loading)
        try {
            val dtoList = statementApiService.getCusLoanSOADetails(loanId)
            val domainList = dtoList.map { dto ->
                val cleanParticular = (dto.perticular ?: "Transaction Detail")
                    .replace("\\N", "-")
                    .replace("\\\\N", "-")
                    .trim()

                LoanSOADetail(
                    voucherDate = dto.voucherDate?.trim()?.takeIf { it.isNotEmpty() } ?: "N/A",
                    particular = if (cleanParticular.isEmpty()) "Transaction Detail" else cleanParticular,
                    debit = dto.debit ?: 0.0,
                    credit = dto.credit ?: 0.0,
                    balance = dto.balance ?: 0.0
                )
            }
            emit(Result.Success(domainList))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    /**
     * Legacy / Download statement list fallback.
     */
    override fun getStatements(loanAccountId: String): Flow<Result<List<Statement>>> = flow {
        emit(Result.Loading)
        try {
            val mockList = listOf(
                Statement(
                    id = "s1",
                    period = "July 2026",
                    fromDate = "2026-07-01",
                    toDate = "2026-07-31",
                    generatedDate = "2026-07-15",
                    openingBalance = 3762500.0,
                    closingBalance = 3750000.0,
                    totalPaid = 39500.0,
                    type = StatementType.MONTHLY,
                    pdfUrl = "https://mentorhomeloans.com/statements/pdf/s1",
                    csvUrl = "https://mentorhomeloans.com/statements/csv/s1",
                    sizeKb = 120
                ),
                Statement(
                    id = "s2",
                    period = "Annual Statement FY 2025-26",
                    fromDate = "2025-04-01",
                    toDate = "2026-03-31",
                    generatedDate = "2026-04-02",
                    openingBalance = 4300000.0,
                    closingBalance = 3850000.0,
                    totalPaid = 474000.0,
                    type = StatementType.ANNUAL,
                    pdfUrl = "https://mentorhomeloans.com/statements/pdf/s4",
                    csvUrl = "https://mentorhomeloans.com/statements/csv/s4",
                    sizeKb = 1240
                )
            )
            emit(Result.Success(mockList))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun downloadStatementPdf(statement: Statement): Result<String> {
        return Result.Success("/storage/emulated/0/Download/Mentor_${statement.period.replace(" ", "_")}.pdf")
    }

    override suspend fun downloadStatementCsv(statement: Statement): Result<String> {
        return Result.Success("/storage/emulated/0/Download/Mentor_${statement.period.replace(" ", "_")}.csv")
    }
}
