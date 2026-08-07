package com.mentorhomeloans.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing cached LoanAccount details.
 */
@Entity(tableName = "loan_accounts")
data class LoanAccountEntity(
    @PrimaryKey val id: String,
    val accountNumber: String,
    val loanType: String,
    val sanctionAmount: Double,
    val disbursedAmount: Double,
    val outstandingAmount: Double,
    val interestRate: Double,
    val tenure: Int,
    val remainingTenure: Int,
    val emiAmount: Double,
    val nextEmiDate: String,
    val nextEmiAmount: Double,
    val isOverdue: Boolean,
    val overdueAmount: Double,
    val overdueEmiCount: Int,
    val startDate: String,
    val maturityDate: String,
    val paidEmiCount: Int,
    val totalEmiCount: Int,
    val status: String,
    val branchName: String,
    val loanManagerName: String
)
