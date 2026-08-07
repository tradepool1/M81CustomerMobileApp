package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.GetLoanDetailsRequestDto
import com.mentorhomeloans.data.remote.dto.GetLoanDetailsResponseItemDto
import com.mentorhomeloans.data.remote.dto.LoanAccountDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Loan account details REST interface endpoints.
 */
interface LoanApiService {

    @GET("customers/{customerId}/loans/active")
    suspend fun getLoanAccount(
        @Path("customerId") customerId: String
    ): ApiResponse<LoanAccountDto>

    @Headers("Content-Type: application/json-patch+json")
    @POST("Get_All_Loan_Details_By_Customer_Phone_Number")
    suspend fun getAllLoanDetails(
        @Body request: GetLoanDetailsRequestDto
    ): List<GetLoanDetailsResponseItemDto>
}
