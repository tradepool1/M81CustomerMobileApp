package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Notification response schema.
 */
data class NotificationDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("type") val type: String,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("receivedAt") val receivedAt: String,
    @SerializedName("actionUrl") val actionUrl: String?,
    @SerializedName("iconType") val iconType: String,
    @SerializedName("metaData") val metaData: Map<String, String>?
)
