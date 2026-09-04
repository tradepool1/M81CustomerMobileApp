package com.mentorhomeloans.core.network

import com.google.gson.GsonBuilder
import com.mentorhomeloans.BuildConfig
import com.mentorhomeloans.core.common.Constants
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import com.mentorhomeloans.data.remote.api.AuthApiService

/**
 * Helper client to construct Retrofit configurations.
 */
object ApiClient {

    /**
     * Builds and returns a new Retrofit instance.
     *
     * @param authInterceptor       Adds Authorization + device-info headers to every request.
     * @param authenticator         Handles automatic token refresh on 401 responses.
     * @param apiLoggingInterceptor Logs full request/response details via Timber (debug only).
     */
    fun createRetrofit(
        authInterceptor: AuthInterceptor,
        authenticator: Authenticator,
        apiLoggingInterceptor: ApiLoggingInterceptor
    ): Retrofit {

        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(Constants.API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.API_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.API_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .authenticator(authenticator)

        // ── Logging: only in debug builds ─────────────────────────────────────
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(apiLoggingInterceptor)
        }

        // ── Debug-only: trust self-signed / LAN dev server certs ──────────────
        if (BuildConfig.TRUST_ALL_CERTS) {
            val trustAllManager = object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
            val sslContext = SSLContext.getInstance("TLS").apply {
                init(null, arrayOf<TrustManager>(trustAllManager), SecureRandom())
            }
            clientBuilder
                .sslSocketFactory(sslContext.socketFactory, trustAllManager)
                .hostnameVerifier { _, _ -> true }
        }

        val okHttpClient = clientBuilder.build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Creates a specialized service for token refresh that does not have an authenticator
     * to avoid infinite loops during refresh.
     */
    fun createRefreshService(
        authInterceptor: AuthInterceptor,
        apiLoggingInterceptor: ApiLoggingInterceptor
    ): AuthApiService {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(Constants.API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(apiLoggingInterceptor)
        }

        if (BuildConfig.TRUST_ALL_CERTS) {
            val trustAllManager = object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
            val sslContext = SSLContext.getInstance("TLS").apply {
                init(null, arrayOf<TrustManager>(trustAllManager), SecureRandom())
            }
            clientBuilder
                .sslSocketFactory(sslContext.socketFactory, trustAllManager)
                .hostnameVerifier { _, _ -> true }
        }

        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AuthApiService::class.java)
    }
}
