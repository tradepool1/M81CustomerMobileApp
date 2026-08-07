package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.Statement
import com.mentorhomeloans.domain.model.StatementType
import com.mentorhomeloans.domain.repository.StatementRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MockStatementRepository details provider.
 */
@Singleton
class MockStatementRepository @Inject constructor() : StatementRepository {

    private val mockList = listOf(
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
            period = "June 2026",
            fromDate = "2026-06-01",
            toDate = "2026-06-30",
            generatedDate = "2026-07-01",
            openingBalance = 3775000.0,
            closingBalance = 3762500.0,
            totalPaid = 39500.0,
            type = StatementType.MONTHLY,
            pdfUrl = "https://mentorhomeloans.com/statements/pdf/s2",
            csvUrl = "https://mentorhomeloans.com/statements/csv/s2",
            sizeKb = 118
        ),
        Statement(
            id = "s3",
            period = "Q1 FY 2026-27",
            fromDate = "2026-04-01",
            toDate = "2026-06-30",
            generatedDate = "2026-07-01",
            openingBalance = 3850000.0,
            closingBalance = 3750000.0,
            totalPaid = 118500.0,
            type = StatementType.QUARTERLY,
            pdfUrl = "https://mentorhomeloans.com/statements/pdf/s3",
            csvUrl = "https://mentorhomeloans.com/statements/csv/s3",
            sizeKb = 280
        ),
        Statement(
            id = "s4",
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

    override fun getStatements(loanAccountId: String): Flow<Result<List<Statement>>> = flow {
        emit(Result.Loading)
        delay(800)
        emit(Result.Success(mockList))
    }

    override suspend fun downloadStatementPdf(statement: Statement): Result<String> {
        delay(1200)
        return Result.Success("/storage/emulated/0/Download/Mentor_${statement.period.replace(" ", "_")}.pdf")
    }

    override suspend fun downloadStatementCsv(statement: Statement): Result<String> {
        delay(1000)
        return Result.Success("/storage/emulated/0/Download/Mentor_${statement.period.replace(" ", "_")}.csv")
    }
}
