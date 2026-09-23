package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Item inside GetMasterRecords response for LoanDocuments.
 */
data class MasterLoanDocumentDto(
    @SerializedName("Id") val id: Int,
    @SerializedName("DocumentName") val documentName: String
)

/**
 * Response from GET /api/MobileApp/GetMasterRecords
 */
data class GetMasterRecordsResponseDto(
    @SerializedName("LoanDocuments") val loanDocuments: List<MasterLoanDocumentDto>?
)

/**
 * Request body for POST /api/MobileApp/RequestDocument
 */
data class RequestDocumentRequestDto(
    @SerializedName("customerId") val customerId: String,
    @SerializedName("loanAcNo") val loanAcNo: String,
    @SerializedName("documentTypeId") val documentTypeId: Int
)
