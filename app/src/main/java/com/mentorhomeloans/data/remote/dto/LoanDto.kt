package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request body for Get_All_Loan_Details_By_Customer_Phone_Number endpoint.
 */
data class GetLoanDetailsRequestDto(
    @SerializedName("phoneNo") val phoneNo: String
)

/**
 * Response item for Get_All_Loan_Details_By_Customer_Phone_Number endpoint.
 */
data class GetLoanDetailsResponseItemDto(
    @SerializedName("LoanAcNo") val loanAcNo: String?,
    @SerializedName("PrinciplReceived") val principlReceived: Double?,
    @SerializedName("InterestReceived") val interestReceived: Double?,
    @SerializedName("DisbursementAmt") val disbursementAmt: Double?,
    @SerializedName("NetFinance") val netFinance: Double?,
    @SerializedName("Case_IRR") val caseIRR: Double?,
    @SerializedName("LoanStatus") val loanStatus: String?,
    @SerializedName("POS") val pos: Double?,
    @SerializedName("ReceivedAmt") val receivedAmt: Double?,
    @SerializedName("LoanAmount") val loanAmount: Double?,
    @SerializedName("EMI_DueDate") val emiDueDate: String?,
    @SerializedName("LoanEMIAmount") val loanEMIAmount: Double?
)

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
