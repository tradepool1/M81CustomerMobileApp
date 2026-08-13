package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.CustomerAndCoApplicantDto
import com.mentorhomeloans.data.remote.dto.UserDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Customer profile related REST interface.
 */
interface ProfileApiService {

    @GET("customers/{customerId}/profile")
    suspend fun getProfile(
        @Path("customerId") customerId: String
    ): ApiResponse<UserDto>

    @FormUrlEncoded
    @POST("customers/{customerId}/profile/update-email")
    suspend fun updateEmail(
        @Path("customerId") customerId: String,
        @Field("email") email: String
    ): ApiResponse<Unit>

    @POST("Get_CustomerAndCoApplicant_Details")
    suspend fun getCustomerAndCoApplicantDetails(
        @Query("loanId") loanId: String
    ): List<CustomerAndCoApplicantDto>
}
