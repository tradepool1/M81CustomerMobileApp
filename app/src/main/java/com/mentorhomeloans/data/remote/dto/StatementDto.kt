package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Remote DTO mapping Statement entities.
 */
data class StatementDto(
    @SerializedName("id") val id: String,
    @SerializedName("period") val period: String,
    @SerializedName("fromDate") val fromDate: String,
    @SerializedName("toDate") val toDate: String,
    @SerializedName("generatedDate") val generatedDate: String,
    @SerializedName("openingBalance") val openingBalance: Double,
    @SerializedName("closingBalance") val closingBalance: Double,
    @SerializedName("totalPaid") val totalPaid: Double,
    @SerializedName("type") val type: String,
    @SerializedName("pdfUrl") val pdfUrl: String,
    @SerializedName("csvUrl") val csvUrl: String,
    @SerializedName("sizeKb") val sizeKb: Int
)
