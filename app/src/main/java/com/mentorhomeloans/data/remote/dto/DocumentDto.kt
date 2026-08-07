package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO detailing document elements returned by remote APIs.
 */
data class DocumentDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("type") val type: String,
    @SerializedName("uploadedDate") val uploadedDate: String,
    @SerializedName("fileUrl") val fileUrl: String,
    @SerializedName("fileType") val fileType: String,
    @SerializedName("sizeKb") val sizeKb: Int,
    @SerializedName("isPasswordProtected") val isPasswordProtected: Boolean,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String?
)
