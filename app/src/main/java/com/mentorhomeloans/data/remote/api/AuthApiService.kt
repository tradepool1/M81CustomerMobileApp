package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.SendOtpResponseDto
import com.mentorhomeloans.data.remote.dto.VerifyOtpResponseDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Authentication REST endpoints definition.
 */
interface AuthApiService {

    @FormUrlEncoded
    @POST("auth/send-otp")
    suspend fun sendOtp(
        @Field("mobileNumber") mobileNumber: String
    ): ApiResponse<SendOtpResponseDto>

    @FormUrlEncoded
    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Field("mobileNumber") mobileNumber: String,
        @Field("otp") otp: String,
        @Field("sessionId") sessionId: String
    ): ApiResponse<VerifyOtpResponseDto>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>
}
