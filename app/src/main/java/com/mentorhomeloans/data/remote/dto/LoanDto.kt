package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO structure mapping remote LoanAccount entity properties.
 */
data class LoanAccountDto(
    @SerializedName("id") val id: String,
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("loanType") val loanType: String,
    @SerializedName("sanctionAmount") val sanctionAmount: Double,
    @SerializedName("disbursedAmount") val disbursedAmount: Double,
    @SerializedName("outstandingAmount") val outstandingAmount: Double,
    @SerializedName("interestRate") val interestRate: Double,
    @SerializedName("tenure") val tenure: Int,
    @SerializedName("remainingTenure") val remainingTenure: Int,
    @SerializedName("emiAmount") val emiAmount: Double,
    @SerializedName("nextEmiDate") val nextEmiDate: String,
    @SerializedName("nextEmiAmount") val nextEmiAmount: Double,
    @SerializedName("isOverdue") val isOverdue: Boolean,
    @SerializedName("overdueAmount") val overdueAmount: Double,
    @SerializedName("overdueEmiCount") val overdueEmiCount: Int,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("maturityDate") val maturityDate: String,
    @SerializedName("paidEmiCount") val paidEmiCount: Int,
    @SerializedName("totalEmiCount") val totalEmiCount: Int,
    @SerializedName("status") val status: String,
    @SerializedName("branchName") val branchName: String,
    @SerializedName("loanManagerName") val loanManagerName: String
)
