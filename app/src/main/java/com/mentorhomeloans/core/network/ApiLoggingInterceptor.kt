package com.mentorhomeloans.core.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import timber.log.Timber
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that logs every API request and response in a clean,
 * human-readable format using Timber.
 *
 * Logs include:
 *  Request  → method, URL, all headers, query params, body
 *  Response → status code, latency, all headers, body
 *
 * Only active in DEBUG builds (controlled by the caller in ApiClient).
 */
@Singleton
class ApiLoggingInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request  = chain.request()
        val startNs  = System.nanoTime()

        // ── REQUEST ──────────────────────────────────────────────────────────
        val sb = StringBuilder()
        sb.appendLine()
        sb.appendLine("┌──────────────────────────────────────────────────────────────")
        sb.appendLine("│ ➤ REQUEST")
        sb.appendLine("│ Method  : ${request.method}")
        sb.appendLine("│ URL     : ${request.url}")

        // Query params
        val querySize = request.url.querySize
        if (querySize > 0) {
            sb.appendLine("│ Params  :")
            for (i in 0 until querySize) {
                sb.appendLine("│   ${request.url.queryParameterName(i)} = ${request.url.queryParameterValue(i)}")
            }
        }

        // Request headers
        sb.appendLine("│ Headers :")
        request.headers.forEach { (name, value) ->
            sb.appendLine("│   $name: $value")
        }

        // Request body
        val requestBody = request.body
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val charset = requestBody.contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
            val bodyString = buffer.readString(charset)
            sb.appendLine("│ Body    :")
            sb.appendLine("│   $bodyString")
        } else {
            sb.appendLine("│ Body    : (none)")
        }

        sb.appendLine("└──────────────────────────────────────────────────────────────")
        Timber.tag("API_REQUEST").d(sb.toString())

        // ── EXECUTE ───────────────────────────────────────────────────────────
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Timber.tag("API_REQUEST").e("✗ Request FAILED: ${e.message}")
            throw e
        }

        val elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        // ── RESPONSE ─────────────────────────────────────────────────────────
        val rs = StringBuilder()
        rs.appendLine()
        rs.appendLine("┌──────────────────────────────────────────────────────────────")
        rs.appendLine("│ ◀ RESPONSE")
        rs.appendLine("│ URL        : ${response.request.url}")
        rs.appendLine("│ Status     : ${response.code} ${response.message}")
        rs.appendLine("│ Latency    : ${elapsedMs}ms")

        // Response headers
        rs.appendLine("│ Headers    :")
        response.headers.forEach { (name, value) ->
            rs.appendLine("│   $name: $value")
        }

        // Response body — peek so we don't consume the stream
        val responseBody = response.body
        if (response.promisesBody() && responseBody != null) {
            val source  = responseBody.source()
            source.request(Long.MAX_VALUE)               // buffer all bytes
            val buffer  = source.buffer.clone()
            val charset: Charset = responseBody.contentType()
                ?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
            val bodyString = buffer.readString(charset)
            rs.appendLine("│ Body       :")
            rs.appendLine("│   $bodyString")
        } else {
            rs.appendLine("│ Body       : (none / not readable)")
        }

        rs.appendLine("└──────────────────────────────────────────────────────────────")
        Timber.tag("API_RESPONSE").d(rs.toString())

        return response
    }
}
