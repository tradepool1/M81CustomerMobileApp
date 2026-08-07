package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.TransactionDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST Endpoint for transactions history list data.
 */
interface TransactionApiService {

    @GET("loans/{loanAccountId}/transactions")
    suspend fun getTransactions(
        @Path("loanAccountId") loanAccountId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiResponse<List<TransactionDto>>

    @GET("loans/{loanAccountId}/transactions/search")
    suspend fun searchTransactions(
        @Path("loanAccountId") loanAccountId: String,
        @Query("query") query: String
    ): ApiResponse<List<TransactionDto>>
}
