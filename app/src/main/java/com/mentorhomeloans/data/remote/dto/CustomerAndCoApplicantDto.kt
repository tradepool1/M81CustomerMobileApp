package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO matching the server JSON response of Get_CustomerAndCoApplicant_Details endpoint.
 */
data class CustomerAndCoApplicantDto(
    @SerializedName("Int_Id") val intId: Long?,
    @SerializedName("KYC_DocId") val kycDocId: Int?,
    @SerializedName("KYC_DocName") val kycDocName: String?,
    @SerializedName("KYC_DocNumber") val kycDocNumber: String?,
    @SerializedName("CustomerId") val customerId: Long?,
    @SerializedName("CustomerName") val customerName: String?,
    @SerializedName("CustomerType") val customerType: String?,
    @SerializedName("Customer_IsFirm") val customerIsFirm: Int?,
    @SerializedName("CustomerPhone") val customerPhone: String?,
    @SerializedName("Present_Address") val presentAddress: String?,
    @SerializedName("CustomerEmail") val customerEmail: String?,
    @SerializedName("GenderAge") val genderAge: String?,
    @SerializedName("ExistingCustomer") val existingCustomer: String?,
    @SerializedName("Relation_With_Hirer") val relationWithHirer: String?
)
