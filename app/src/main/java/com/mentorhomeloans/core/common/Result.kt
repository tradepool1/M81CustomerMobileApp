package com.mentorhomeloans.core.common

/**
 * A generic sealed class representing the state of a data operation.
 *
 * Used across all layers to propagate loading, success, and error states
 * in a type-safe, exhaustive manner.
 *
 * @param T The type of data wrapped on success.
 */
sealed class Result<out T> {

    /** Represents an in-progress operation. */
    object Loading : Result<Nothing>()

    /**
     * Represents a successful operation.
     * @property data The result payload.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation.
     * @property exception The underlying exception.
     * @property message   A human-readable error string derived from the exception.
     */
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "An unknown error occurred"
    ) : Result<Nothing>()
}
