package com.mentorhomeloans.data.remote.api

import com.mentorhomeloans.data.remote.dto.ApiResponse
import com.mentorhomeloans.data.remote.dto.NotificationDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Push notifications REST endpoints interface.
 */
interface NotificationApiService {

    @GET("customers/{customerId}/notifications")
    suspend fun getNotifications(
        @Path("customerId") customerId: String
    ): ApiResponse<List<NotificationDto>>

    @POST("notifications/{notificationId}/read")
    suspend fun markAsRead(
        @Path("notificationId") notificationId: String
    ): ApiResponse<Unit>

    @FormUrlEncoded
    @POST("customers/{customerId}/notifications/read-all")
    suspend fun markAllAsRead(
        @Path("customerId") customerId: String
    ): ApiResponse<Int>
}
