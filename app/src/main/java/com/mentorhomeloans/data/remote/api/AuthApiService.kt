package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.GenerateOtpRequestDto
import com.mentorhomeloans.data.remote.dto.GenerateOtpResponseDto
import com.mentorhomeloans.data.remote.dto.OtpVerificationRequestDto
import com.mentorhomeloans.data.remote.dto.OtpVerificationResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Authentication REST endpoints definition.
 * Base URL: https://192.168.200.11:4204/api/MobileApp/
 */
interface AuthApiService {

    /**
     * Sends OTP to the given phone number.
     * POST /api/MobileApp/GenerateOTP
     * Body: { "phoneNo": "..." }
     */
    @Headers("Content-Type: application/json-patch+json")
    @POST("GenerateOTP")
    suspend fun generateOtp(
        @Body request: GenerateOtpRequestDto
    ): GenerateOtpResponseDto

    /**
     * Verifies OTP code for the given phone number.
     * POST /api/MobileApp/OTP_Verification
     * Body: { "phoneNo": "...", "otpCode": "..." }
     */
    @Headers("Content-Type: application/json-patch+json")
    @POST("OTP_Verification")
    suspend fun verifyOtp(
        @Body request: OtpVerificationRequestDto
    ): OtpVerificationResponseDto
}
