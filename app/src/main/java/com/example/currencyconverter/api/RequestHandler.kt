package com.example.currencyconverter.api

import com.example.currencyconverter.api.models.RequestResource
import retrofit2.Response

abstract class RequestHandler {
    suspend fun <T> executeRequestWithHandling(apiCall: suspend () -> Response<T>): RequestResource<T> {

        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return RequestResource.success(body)
                }
            }
            return throwError("Something went wrong: ${response.message()}")
        } catch (e: Exception) {
            return throwError(e.message ?: e.toString())
        }
    }

    private fun <T> throwError(message: String): RequestResource<T> {
        return RequestResource.error("Something went wrong: $message")
    }
}