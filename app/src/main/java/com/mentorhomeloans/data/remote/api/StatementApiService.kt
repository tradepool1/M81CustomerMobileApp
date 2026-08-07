package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.StatementDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Statement REST endpoint declarations mapping.
 */
interface StatementApiService {

    @GET("loans/{loanAccountId}/statements")
    suspend fun getStatements(
        @Path("loanAccountId") loanAccountId: String
    ): ApiResponse<List<StatementDto>>
}
