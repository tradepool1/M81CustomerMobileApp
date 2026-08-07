package com.mentorhomeloans.core.network

import java.io.IOException

/**
 * Custom base exception class for network-related failures.
 *
 * @param message Human-readable error description.
 * @param cause Underlying exception if available.
 */
open class NetworkException(message: String, cause: Throwable? = null) : IOException(message, cause)

/**
 * Thrown when the device is completely offline.
 */
class NoNetworkException(message: String = "No internet connection detected") : NetworkException(message)

/**
 * Thrown when a remote API call returns a non-2xx status code.
 *
 * @property code HTTP response status code.
 * @property errorBody Raw error response body from backend.
 */
class ApiException(val code: Int, val errorBody: String?, message: String) : NetworkException(message)

/**
 * Thrown when a request times out (connect, read, or write).
 */
class TimeoutException(message: String = "Request timed out") : NetworkException(message)
