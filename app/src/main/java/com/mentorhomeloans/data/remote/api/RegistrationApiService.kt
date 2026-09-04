package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.BranchDto
import com.mentorhomeloans.data.remote.dto.RegistrationRequestDto
import com.mentorhomeloans.data.remote.dto.RegistrationResponseDto
import com.mentorhomeloans.data.remote.dto.StateDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Registration REST endpoints definition.
 * Base URL: https://192.168.200.11:4204/api/MobileApp/
 */
interface RegistrationApiService {

    /**
     * Fetches list of available states.
     * GET /api/MobileApp/GetState
     */
    @GET("GetState")
    suspend fun getStates(): List<StateDto>

    /**
     * Fetches branches filtered by state.
     * POST /api/MobileApp/GetBranchesByStateId?stateId={stateId}
     */
    @POST("GetBranchesByStateId")
    suspend fun getBranchesByStateId(
        @Query("stateId") stateId: Int
    ): List<BranchDto>

    /**
     * Submits customer app registration request.
     * POST /api/MobileApp/CustomerAppRegistration
     */
    @Headers("Content-Type: application/json-patch+json")
    @POST("CustomerAppRegistration")
    suspend fun registerCustomer(
        @Body request: RegistrationRequestDto
    ): RegistrationResponseDto
}
