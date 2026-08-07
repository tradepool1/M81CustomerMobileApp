package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request body for GenerateOTP endpoint.
 */
data class GenerateOtpRequestDto(
    @SerializedName("phoneNo") val phoneNo: String
)

/**
 * Response from GenerateOTP endpoint.
 * { "Status": true, "Message": "OTP sent successfully." }
 */
data class GenerateOtpResponseDto(
    @SerializedName("Status")  val status: Boolean,
    @SerializedName("Message") val message: String
)

/**
 * Request body for OTP_Verification endpoint.
 */
data class OtpVerificationRequestDto(
    @SerializedName("phoneNo")  val phoneNo: String,
    @SerializedName("otpCode")  val otpCode: String
)

/**
 * Response from OTP_Verification endpoint.
 * { "Status": true, "Message": "...", "Token": "eyJ..." }
 */
data class OtpVerificationResponseDto(
    @SerializedName("Status")  val status: Boolean,
    @SerializedName("Message") val message: String,
    @SerializedName("Token")   val token: String?
)
