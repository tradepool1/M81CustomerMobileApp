package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.domain.model.LoanSOADetail
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

    override fun getCusLoanSOADetails(loanId: String): Flow<Result<List<LoanSOADetail>>> = flow {
        emit(Result.Loading)
        delay(600)
        val mockSoa = listOf(
            LoanSOADetail("30 Nov 2023", "Login Fee Due", 1300.0, 0.0, 1300.0),
            LoanSOADetail("30 Nov 2023", "Amount Received Mode - Cash Instrument NO - - Instrument Amount - 1300.0000 TDS - 0.0000 Txn Date - 2023-11-30 Value Date - 2023-11-30. Receipt No-85214", 0.0, 1300.0, 0.0),
            LoanSOADetail("13 Dec 2023", "Home Loans Payable", 0.0, 325000.0, -325000.0),
            LoanSOADetail("13 Dec 2023", "**Amount Paid Mode - NEFT Instrument NO - DC1011 Instrument Amount - 325000.0000 TDS - 0.0000 Txn Date - 2023-12-13 Value Date - 2023-12-13", 325000.0, 0.0, 0.0),
            LoanSOADetail("30 Dec 2023", "Home Loans Payable", 0.0, 325000.0, -325000.0),
            LoanSOADetail("30 Dec 2023", "**Amount Paid Mode - NEFT Instrument NO - DC1039 Instrument Amount - 325000.0000 TDS - 0.0000 Txn Date - 2023-12-30 Value Date - 2023-12-30", 325000.0, 0.0, 0.0),
            LoanSOADetail("10 Jan 2024", "Pre-emi Interest Due", 6681.0, 0.0, 6681.0),
            LoanSOADetail("10 Feb 2024", "Installment Amount Due Installment No-1", 12562.0, 0.0, 19243.0),
            LoanSOADetail("10 Mar 2024", "Installment Amount Due Installment No-2", 12562.0, 0.0, 31805.0),
            LoanSOADetail("10 Apr 2024", "Installment Amount Due Installment No-3", 12562.0, 0.0, 44367.0),
            LoanSOADetail("10 May 2024", "Installment Amount Due Installment No-4", 12562.0, 0.0, 56929.0),
            LoanSOADetail("10 Jun 2024", "Installment Amount Due Installment No-5", 12562.0, 0.0, 69491.0),
            LoanSOADetail("10 Jul 2024", "Installment Amount Due Installment No-6", 12562.0, 0.0, 82053.0),
            LoanSOADetail("10 Aug 2024", "Installment Amount Due Installment No-7", 12562.0, 0.0, 94615.0),
            LoanSOADetail("10 Sep 2024", "Installment Amount Due Installment No-8", 12562.0, 0.0, 107177.0),
            LoanSOADetail("10 Oct 2024", "Installment Amount Due Installment No-9", 12562.0, 0.0, 119739.0),
            LoanSOADetail("14 Oct 2024", "Amount Received Mode - NEFT Instrument NO - 587514 Instrument Amount - 99000.0000 TDS - 0.0000 Txn Date - 2024-10-14 Value Date - 2024-10-14. Receipt No-", 0.0, 99000.0, 20739.0),
            LoanSOADetail("10 Nov 2024", "Installment Amount Due Installment No-10", 12562.0, 0.0, 33301.0),
            LoanSOADetail("11 Nov 2024", "Amount Received Mode - NEFT Instrument NO - 230094 Instrument Amount - 58425.0000 TDS - 0.0000 Txn Date - 2024-11-11 Value Date - 2024-11-11. Receipt No-", 0.0, 58425.0, -25124.0),
            LoanSOADetail("10 Dec 2024", "Installment Amount Due Installment No-11", 2215.0, 0.0, -22909.0),
            LoanSOADetail("04 Jan 2025", "Amount Received Mode - NEFT Instrument NO - MAV004266 Instrument Amount - 25124.0000 TDS - 0.0000 Txn Date - 2025-01-04 Value Date - 2025-01-04. Receipt No-", 0.0, 25124.0, -48033.0),
            LoanSOADetail("10 Jan 2025", "Installment Amount Due Installment No-12", 12562.0, 0.0, -35471.0),
            LoanSOADetail("10 Feb 2025", "Installment Amount Due Installment No-13", 12562.0, 0.0, -22909.0),
            LoanSOADetail("05 Mar 2025", "Amount Received Mode - NEFT Instrument NO - MAV004506 Instrument Amount - 570562.0000 TDS - 0.0000 Txn Date - 2025-03-05 Value Date - 2025-03-05. Receipt No-", 0.0, 570562.0, -593471.0),
            LoanSOADetail("10 Mar 2025", "Installment Amount Due Installment No-14", 12562.0, 0.0, -580909.0),
            LoanSOADetail("10 Apr 2025", "Installment Amount Due Installment No-15", 12562.0, 0.0, -568347.0),
            LoanSOADetail("17 Apr 2025", "Interest Waive Off Due", 570562.0, 0.0, 2215.0),
            LoanSOADetail("17 Apr 2025", "Interest Waive Off Payable", 0.0, 633376.0, -631161.0),
            LoanSOADetail("05 May 2025", "Balance Principal Due", 620814.0, 0.0, -10347.0),
            LoanSOADetail("05 May 2025", "Interest Component Due", 10347.0, 0.0, 0.0)
        )
        emit(Result.Success(mockSoa))
    }


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
