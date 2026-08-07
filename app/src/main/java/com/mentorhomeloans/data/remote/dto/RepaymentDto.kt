package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Single schedule entry representation.
 */
data class RepaymentScheduleItemDto(
    @SerializedName("installmentNumber") val installmentNumber: Int,
    @SerializedName("dueDate") val dueDate: String,
    @SerializedName("emiAmount") val emiAmount: Double,
    @SerializedName("principalAmount") val principalAmount: Double,
    @SerializedName("interestAmount") val interestAmount: Double,
    @SerializedName("charges") val charges: Double,
    @SerializedName("openingBalance") val openingBalance: Double,
    @SerializedName("closingBalance") val closingBalance: Double,
    @SerializedName("status") val status: String,
    @SerializedName("paidDate") val paidDate: String?,
    @SerializedName("paidAmount") val paidAmount: Double?
)

/**
 * Top level Repayment response details.
 */
data class RepaymentSummaryDto(
    @SerializedName("loanAccountNumber") val loanAccountNumber: String,
    @SerializedName("totalLoanAmount") val totalLoanAmount: Double,
    @SerializedName("totalAmountPaid") val totalAmountPaid: Double,
    @SerializedName("totalAmountPending") val totalAmountPending: Double,
    @SerializedName("totalPrincipalPaid") val totalPrincipalPaid: Double,
    @SerializedName("totalInterestPaid") val totalInterestPaid: Double,
    @SerializedName("totalPrincipalPending") val totalPrincipalPending: Double,
    @SerializedName("totalInterestPending") val totalInterestPending: Double,
    @SerializedName("prepaymentAllowed") val prepaymentAllowed: Boolean,
    @SerializedName("prepaymentPenaltyPct") val prepaymentPenaltyPct: Double,
    @SerializedName("foreClosureAmount") val foreClosureAmount: Double,
    @SerializedName("schedule") val schedule: List<RepaymentScheduleItemDto>
)
