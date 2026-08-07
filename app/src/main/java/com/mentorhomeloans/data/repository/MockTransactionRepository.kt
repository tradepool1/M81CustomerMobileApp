package com.mentorhomeloans.data.repository

import com.mentorhomeloans.core.common.Result
import com.mentorhomeloans.data.local.dao.TransactionDao
import com.mentorhomeloans.data.mapper.TransactionMapper
import com.mentorhomeloans.domain.model.PaymentMode
import com.mentorhomeloans.domain.model.Transaction
import com.mentorhomeloans.domain.model.TransactionStatus
import com.mentorhomeloans.domain.model.TransactionType
import com.mentorhomeloans.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MockTransactionRepository details provider.
 */
@Singleton
class MockTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    private val mockList = listOf(
        Transaction(
            id = "t1",
            transactionDate = "2026-07-05",
            valueDate = "2026-07-05",
            type = TransactionType.EMI_PAYMENT,
            description = "EMI Paid - Auto Debit NACH",
            amount = 39500.0,
            principalComponent = 12500.0,
            interestComponent = 27000.0,
            chargesComponent = 0.0,
            balance = 3750000.0,
            referenceNumber = "TXN8892019",
            paymentMode = PaymentMode.NACH,
            status = TransactionStatus.SUCCESS,
            receiptUrl = "https://mentorhomeloans.com/receipt/t1"
        ),
        Transaction(
            id = "t2",
            transactionDate = "2026-06-05",
            valueDate = "2026-06-05",
            type = TransactionType.EMI_PAYMENT,
            description = "EMI Paid - Auto Debit NACH",
            amount = 39500.0,
            principalComponent = 12400.0,
            interestComponent = 27100.0,
            chargesComponent = 0.0,
            balance = 3762500.0,
            referenceNumber = "TXN8871092",
            paymentMode = PaymentMode.NACH,
            status = TransactionStatus.SUCCESS,
            receiptUrl = null
        ),
        Transaction(
            id = "t3",
            transactionDate = "2026-05-18",
            valueDate = "2026-05-19",
            type = TransactionType.PENAL_CHARGE,
            description = "Penal Charge - Late Payment Fees",
            amount = 450.0,
            principalComponent = 0.0,
            interestComponent = 0.0,
            chargesComponent = 450.0,
            balance = 3774900.0,
            referenceNumber = "TXN8862109",
            paymentMode = PaymentMode.SYSTEM,
            status = TransactionStatus.SUCCESS,
            receiptUrl = null
        ),
        Transaction(
            id = "t4",
            transactionDate = "2026-05-05",
            valueDate = "2026-05-05",
            type = TransactionType.EMI_PAYMENT,
            description = "EMI Paid - Auto Debit NACH",
            amount = 39500.0,
            principalComponent = 12300.0,
            interestComponent = 27200.0,
            chargesComponent = 0.0,
            balance = 3775350.0,
            referenceNumber = "TXN8841023",
            paymentMode = PaymentMode.NACH,
            status = TransactionStatus.BOUNCED,
            receiptUrl = null
        ),
        Transaction(
            id = "t5",
            transactionDate = "2026-04-15",
            valueDate = "2026-04-15",
            type = TransactionType.PREPAYMENT,
            description = "Part Prepayment via NEFT",
            amount = 150000.0,
            principalComponent = 150000.0,
            interestComponent = 0.0,
            chargesComponent = 0.0,
            balance = 3814850.0,
            referenceNumber = "TXN8830019",
            paymentMode = PaymentMode.NEFT,
            status = TransactionStatus.SUCCESS,
            receiptUrl = "https://mentorhomeloans.com/receipt/t5"
        ),
        Transaction(
            id = "t6",
            transactionDate = "2026-04-05",
            valueDate = "2026-04-05",
            type = TransactionType.EMI_PAYMENT,
            description = "EMI Paid - NACH",
            amount = 39500.0,
            principalComponent = 12200.0,
            interestComponent = 27300.0,
            chargesComponent = 0.0,
            balance = 3964850.0,
            referenceNumber = "TXN8820991",
            paymentMode = PaymentMode.NACH,
            status = TransactionStatus.SUCCESS,
            receiptUrl = null
        ),
        Transaction(
            id = "t7",
            transactionDate = "2026-03-20",
            valueDate = "2026-03-20",
            type = TransactionType.BOUNCE_CHARGE,
            description = "NACH Bounce Charge",
            amount = 590.0,
            principalComponent = 0.0,
            interestComponent = 0.0,
            chargesComponent = 590.0,
            balance = 4004350.0,
            referenceNumber = "TXN8810124",
            paymentMode = PaymentMode.SYSTEM,
            status = TransactionStatus.SUCCESS,
            receiptUrl = null
        ),
        Transaction(
            id = "t8",
            transactionDate = "2026-03-05",
            valueDate = "2026-03-05",
            type = TransactionType.EMI_PAYMENT,
            description = "EMI Paid - UPI",
            amount = 39500.0,
            principalComponent = 12100.0,
            interestComponent = 27400.0,
            chargesComponent = 0.0,
            balance = 4004940.0,
            referenceNumber = "TXN8800192",
            paymentMode = PaymentMode.UPI,
            status = TransactionStatus.SUCCESS,
            receiptUrl = "https://mentorhomeloans.com/receipt/t8"
        )
    )

    override fun getTransactions(
        loanAccountId: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<List<Transaction>>> = flow {
        emit(Result.Loading)
        transactionDao.insertTransactions(mockList.map { TransactionMapper.toEntity(it, loanAccountId) })

        transactionDao.getTransactions(loanAccountId).collect { entities ->
            emit(Result.Success(entities.map { TransactionMapper.toDomain(it) }))
        }
    }

    override suspend fun filterTransactions(
        loanAccountId: String,
        fromDate: String?,
        toDate: String?,
        types: List<TransactionType>
    ): Result<List<Transaction>> {
        var list = mockList
        if (types.isNotEmpty()) {
            list = list.filter { it.type in types }
        }
        return Result.Success(list)
    }

    override suspend fun searchTransactions(
        loanAccountId: String,
        query: String
    ): Result<List<Transaction>> {
        val result = mockList.filter {
            it.description.contains(query, ignoreCase = true) ||
                    it.referenceNumber.contains(query, ignoreCase = true)
        }
        return Result.Success(result)
    }
}
