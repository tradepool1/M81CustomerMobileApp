package com.mentorhomeloans.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request body for RefreshAccessToken endpoint.
 */
data class RefreshTokenRequestDto(
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("deviceId") val deviceId: String
)

/**
 * Response from OTP_Verification / Login endpoint.
 */
data class OtpVerificationResponseDto(
    @SerializedName("Status")  val status: Boolean,
    @SerializedName("Message") val message: String,
    @SerializedName("AccessToken")  val accessToken: String?,
    @SerializedName("RefreshToken") val refreshToken: String?,
    @SerializedName("AccessTokenExpiresAt") val accessTokenExpiresAt: String?,
    @SerializedName("RefreshTokenExpiresAt") val refreshTokenExpiresAt: String?
)

/**
 * Common response for MobileApp controller endpoints.
 */
data class CommonResponseDto(
    @SerializedName("Status")  val status: Boolean,
    @SerializedName("Message") val message: String?
)

/**
 * Request body for Login endpoint.
 */
data class LoginRequestDto(
    @SerializedName("customerId") val customerId: String,
    @SerializedName("password") val password: String,
    @SerializedName("captchaToken") val captchaToken: String
)

/**
 * Request body for UpdatePassword endpoint.
 */
data class UpdatePasswordRequestDto(
    @SerializedName("customerId") val customerId: String,
    @SerializedName("password") val password: String,
    @SerializedName("captchaToken") val captchaToken: String
)
