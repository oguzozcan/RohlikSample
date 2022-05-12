package com.sample.rohlik.data

import java.io.IOException

sealed class NetworkResponse<out T : Any, out U : Any> {
    /**
     * Success response with body
     */
    data class Success<T : Any>(val body: T) : NetworkResponse<T, Nothing>()

    /**
     * Success response with empty body
     */
    object Empty : NetworkResponse<Nothing, Nothing>()

    /**
     * Not found
     */
    object NotFound : NetworkResponse<Nothing, Nothing>()

    /**
     * Failure response with body
     */
    data class ClientError<U : Any>(val body: U?, val code: Int) : NetworkResponse<Nothing, U>()

    /**
     * Failure response with body
     */
    data class ServerError<U : Any>(val body: U?, val code: Int) : NetworkResponse<Nothing, U>()

    /**
     * Network error
     */
    data class NetworkError(val error: IOException) : NetworkResponse<Nothing, Nothing>()

    /**
     * Generic error indication a failure in serialization or deserialization process.
     */
    data class SerializationError(val error: Throwable?) : NetworkResponse<Nothing, Nothing>()

    /**
     * Other error cases
     */
    data class UnknownError(val error: Throwable?) : NetworkResponse<Nothing, Nothing>()
}