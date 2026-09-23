package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.CommonResponseDto
import com.mentorhomeloans.data.remote.dto.GetMasterRecordsResponseDto
import com.mentorhomeloans.data.remote.dto.RequestDocumentRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Document REST endpoint declarations mapping.
 */
interface DocumentApiService {

    /**
     * Retrieves master records including available loan documents list.
     * GET /api/MobileApp/GetMasterRecords
     */
    @GET("GetMasterRecords")
    suspend fun getMasterRecords(): GetMasterRecordsResponseDto

    /**
     * Requests a loan document for a customer.
     * POST /api/MobileApp/RequestDocument
     */
    @Headers("Content-Type: application/json-patch+json")
    @POST("RequestDocument")
    suspend fun requestDocument(
        @Body request: RequestDocumentRequestDto
    ): CommonResponseDto
}
