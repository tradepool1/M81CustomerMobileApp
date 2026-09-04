package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.GenerateOtpRequestDto
import com.mentorhomeloans.data.remote.dto.GenerateOtpResponseDto
import com.mentorhomeloans.data.remote.dto.OtpVerificationRequestDto
import com.mentorhomeloans.data.remote.dto.OtpVerificationResponseDto
import com.mentorhomeloans.data.remote.dto.PageContentDto
import com.mentorhomeloans.data.remote.dto.RefreshTokenRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

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

    /**
     * Retrieves static page content by page key.
     * GET /api/MobileApp/GetPageContentByPageKey?pageKey=...
     */
    @GET("GetPageContentByPageKey")
    suspend fun getPageContent(
        @Query("pageKey") pageKey: String
    ): List<PageContentDto>

    /**
     * Refreshes the AccessToken using a valid RefreshToken.
     * POST /api/MobileApp/RefreshAccessToken
     */
    @Headers("Content-Type: application/json-patch+json")
    @POST("RefreshAccessToken")
    fun refreshAccessToken(
        @Body request: RefreshTokenRequestDto
    ): Call<OtpVerificationResponseDto>
}
