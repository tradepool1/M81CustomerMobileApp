package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.CommonResponseDto
import com.mentorhomeloans.data.remote.dto.DocumentDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Document REST endpoint declarations mapping.
 */
interface DocumentApiService {

    @GET("loans/{loanAccountId}/documents")
    suspend fun getDocuments(
        @Path("loanAccountId") loanAccountId: String
    ): ApiResponse<List<DocumentDto>>

    @GET("GetLoanDocuments")
    suspend fun getLoanDocuments(
        @Query("loanAcNo") loanAcNo: String
    ): CommonResponseDto
}
