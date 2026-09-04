package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data class representing CMS page content (e.g., About Us, Privacy Policy).
 */
data class PageContentDto(
    @SerializedName("PageKey") val pageKey: String,
    @SerializedName("Title") val title: String,
    @SerializedName("Contents") val contents: String
)
