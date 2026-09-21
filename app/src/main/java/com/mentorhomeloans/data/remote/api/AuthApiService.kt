package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.OtpVerificationResponseDto
import com.mentorhomeloans.data.remote.dto.LoginRequestDto
import com.mentorhomeloans.data.remote.dto.PageContentDto
import com.mentorhomeloans.data.remote.dto.RefreshTokenRequestDto
import com.mentorhomeloans.data.remote.dto.UpdatePasswordRequestDto
import com.mentorhomeloans.data.remote.dto.CommonResponseDto
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
     * Performs customer login with credentials and CAPTCHA token.
     * POST /api/MobileApp/Login
     */
    @Headers("Content-Type: application/json-patch+json")
    @POST("ValidateCustomerCredentials")
    suspend fun login(
        @Body request: LoginRequestDto
    ): OtpVerificationResponseDto

    /**
     * Updates customer password.
     * POST /api/MobileApp/UpdatePassword
     */
    @Headers("Content-Type: application/json-patch+json")
    @POST("UpdatePassword")
    suspend fun updatePassword(
        @Body request: UpdatePasswordRequestDto
    ): CommonResponseDto

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
