package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.DocumentDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Document REST endpoint declarations mapping.
 */
interface DocumentApiService {

    @GET("loans/{loanAccountId}/documents")
    suspend fun getDocuments(
        @Path("loanAccountId") loanAccountId: String
    ): ApiResponse<List<DocumentDto>>
}
