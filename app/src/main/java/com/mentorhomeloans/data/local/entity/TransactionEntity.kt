package com.mentorhomeloans.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing cached Transaction history items.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val loanAccountId: String,
    val transactionDate: String,
    val valueDate: String,
    val type: String,
    val description: String,
    val amount: Double,
    val principalComponent: Double,
    val interestComponent: Double,
    val chargesComponent: Double,
    val balance: Double,
    val referenceNumber: String,
    val paymentMode: String,
    val status: String,
    val receiptUrl: String?
)
