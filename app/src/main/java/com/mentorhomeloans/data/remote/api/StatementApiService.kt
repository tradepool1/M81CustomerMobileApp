package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.LoanSOADetailDto
import com.mentorhomeloans.data.remote.dto.StatementDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Statement REST endpoint declarations mapping.
 */
interface StatementApiService {

    @GET("loans/{loanAccountId}/statements")
    suspend fun getStatements(
        @Path("loanAccountId") loanAccountId: String
    ): ApiResponse<List<StatementDto>>

    /**
     * Fetches Statement of Account (SOA) transaction details for a given loanId.
     * POST /GetCusLoanSOADetails?loanId={loanId}
     */
    @POST("GetCusLoanSOADetails")
    suspend fun getCusLoanSOADetails(
        @Query("loanId") loanId: String
    ): List<LoanSOADetailDto>
}

