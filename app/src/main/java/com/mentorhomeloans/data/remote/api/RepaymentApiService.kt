package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.RepaymentSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Repayment/Amortization schedule REST endpoints.
 */
interface RepaymentApiService {

    @GET("loans/{loanAccountId}/repayments/summary")
    suspend fun getRepaymentSummary(
        @Path("loanAccountId") loanAccountId: String
    ): ApiResponse<RepaymentSummaryDto>

    @GET("loans/{loanAccountId}/repayments/calculate-foreclosure")
    suspend fun calculateForeclosure(
        @Path("loanAccountId") loanAccountId: String,
        @Query("date") date: String
    ): ApiResponse<Double>
}
