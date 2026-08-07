package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO carrying details returned upon requesting login OTP.
 */
data class SendOtpResponseDto(
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("otpLength") val otpLength: Int,
    @SerializedName("expirySeconds") val expirySeconds: Int
)

/**
 * DTO carrying login details returned upon verifying login OTP.
 */
data class VerifyOtpResponseDto(
    @SerializedName("jwtToken") val jwtToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("mobileNumber") val mobileNumber: String
)
