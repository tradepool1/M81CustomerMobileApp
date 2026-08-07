package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Remote DTO mapping Transaction entity parameters.
 */
data class TransactionDto(
    @SerializedName("id") val id: String,
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("valueDate") val valueDate: String,
    @SerializedName("type") val type: String,
    @SerializedName("description") val description: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("principalComponent") val principalComponent: Double,
    @SerializedName("interestComponent") val interestComponent: Double,
    @SerializedName("chargesComponent") val chargesComponent: Double,
    @SerializedName("balance") val balance: Double,
    @SerializedName("referenceNumber") val referenceNumber: String,
    @SerializedName("paymentMode") val paymentMode: String,
    @SerializedName("status") val status: String,
    @SerializedName("receiptUrl") val receiptUrl: String?
)
