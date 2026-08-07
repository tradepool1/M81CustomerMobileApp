package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.SupportTicketDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Support ticketing REST endpoints.
 */
interface SupportApiService {

    @GET("customers/{customerId}/tickets")
    suspend fun getTickets(
        @Path("customerId") customerId: String
    ): ApiResponse<List<SupportTicketDto>>

    @GET("tickets/{ticketId}")
    suspend fun getTicketById(
        @Path("ticketId") ticketId: String
    ): ApiResponse<SupportTicketDto>

    @FormUrlEncoded
    @POST("tickets/raise")
    suspend fun raiseTicket(
        @Field("category") category: String,
        @Field("subject") subject: String,
        @Field("description") description: String,
        @Field("attachments") attachments: List<String>
    ): ApiResponse<SupportTicketDto>

    @FormUrlEncoded
    @POST("tickets/{ticketId}/reply")
    suspend fun replyToTicket(
        @Path("ticketId") ticketId: String,
        @Field("message") message: String
    ): ApiResponse<Unit>
}
