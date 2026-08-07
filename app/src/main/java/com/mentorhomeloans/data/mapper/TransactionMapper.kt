package com.mentorhomeloans.data.mapper

import com.mentorhomeloans.data.local.entity.TransactionEntity
import com.mentorhomeloans.domain.model.PaymentMode
import com.mentorhomeloans.domain.model.Transaction
import com.mentorhomeloans.domain.model.TransactionStatus
import com.mentorhomeloans.domain.model.TransactionType

/**
 * Maps Transaction entities to domain objects.
 */
object TransactionMapper {

    fun toDomain(entity: TransactionEntity): Transaction {
        return Transaction(
            id = entity.id,
            transactionDate = entity.transactionDate,
            valueDate = entity.valueDate,
            type = TransactionType.entries.find { it.name == entity.type } ?: TransactionType.EMI_PAYMENT,
            description = entity.description,
            amount = entity.amount,
            principalComponent = entity.principalComponent,
            interestComponent = entity.interestComponent,
            chargesComponent = entity.chargesComponent,
            balance = entity.balance,
            referenceNumber = entity.referenceNumber,
            paymentMode = PaymentMode.entries.find { it.name == entity.paymentMode } ?: PaymentMode.NACH,
            status = TransactionStatus.entries.find { it.name == entity.status } ?: TransactionStatus.SUCCESS,
            receiptUrl = entity.receiptUrl
        )
    }

    fun toEntity(domain: Transaction, loanAccountId: String): TransactionEntity {
        return TransactionEntity(
            id = domain.id,
            loanAccountId = loanAccountId,
            transactionDate = domain.transactionDate,
            valueDate = domain.valueDate,
            type = domain.type.name,
            description = domain.description,
            amount = domain.amount,
            principalComponent = domain.principalComponent,
            interestComponent = domain.interestComponent,
            chargesComponent = domain.chargesComponent,
            balance = domain.balance,
            referenceNumber = domain.referenceNumber,
            paymentMode = domain.paymentMode.name,
            status = domain.status.name,
            receiptUrl = domain.receiptUrl
        )
    }
}
