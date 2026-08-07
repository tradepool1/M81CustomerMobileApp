package com.mentorhomeloans.di

import com.mentorhomeloans.core.network.ApiClient
import com.mentorhomeloans.core.network.AuthInterceptor
import com.mentorhomeloans.data.remote.api.AuthApiService
import com.mentorhomeloans.data.remote.api.DocumentApiService
import com.mentorhomeloans.data.remote.api.LoanApiService
import com.mentorhomeloans.data.remote.api.NotificationApiService
import com.mentorhomeloans.data.remote.api.ProfileApiService
import com.mentorhomeloans.data.remote.api.RepaymentApiService
import com.mentorhomeloans.data.remote.api.StatementApiService
import com.mentorhomeloans.data.remote.api.SupportApiService
import com.mentorhomeloans.data.remote.api.TransactionApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Hilt module providing Retrofit and all API service instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit {
        return ApiClient.createRetrofit(authInterceptor)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideLoanApiService(retrofit: Retrofit): LoanApiService =
        retrofit.create(LoanApiService::class.java)

    @Provides
    @Singleton
    fun provideTransactionApiService(retrofit: Retrofit): TransactionApiService =
        retrofit.create(TransactionApiService::class.java)

    @Provides
    @Singleton
    fun provideStatementApiService(retrofit: Retrofit): StatementApiService =
        retrofit.create(StatementApiService::class.java)

    @Provides
    @Singleton
    fun provideDocumentApiService(retrofit: Retrofit): DocumentApiService =
        retrofit.create(DocumentApiService::class.java)

    @Provides
    @Singleton
    fun provideRepaymentApiService(retrofit: Retrofit): RepaymentApiService =
        retrofit.create(RepaymentApiService::class.java)

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)

    @Provides
    @Singleton
    fun provideSupportApiService(retrofit: Retrofit): SupportApiService =
        retrofit.create(SupportApiService::class.java)

    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService =
        retrofit.create(NotificationApiService::class.java)
}
