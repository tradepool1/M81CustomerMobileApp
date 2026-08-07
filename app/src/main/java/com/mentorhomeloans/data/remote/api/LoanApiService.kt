package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.LoanAccountDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Loan account details REST interface endpoints.
 */
interface LoanApiService {

    @GET("customers/{customerId}/loans/active")
    suspend fun getLoanAccount(
        @Path("customerId") customerId: String
    ): ApiResponse<LoanAccountDto>
}
