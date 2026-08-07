package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Nested profile details schema.
 */
data class AddressDto(
    @SerializedName("line1") val line1: String,
    @SerializedName("line2") val line2: String?,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("pinCode") val pinCode: String,
    @SerializedName("country") val country: String
)

/**
 * Co-applicant structure schema.
 */
data class CoApplicantDto(
    @SerializedName("name") val name: String,
    @SerializedName("relationship") val relationship: String,
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("panNumber") val panNumber: String
)

/**
 * User Profile API response data object.
 */
data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("mobileNumber") val mobileNumber: String,
    @SerializedName("email") val email: String?,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("panNumber") val panNumber: String,
    @SerializedName("address") val address: AddressDto,
    @SerializedName("coApplicant") val coApplicant: CoApplicantDto?,
    @SerializedName("kycStatus") val kycStatus: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
)
