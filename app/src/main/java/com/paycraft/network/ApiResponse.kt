package com.paycraft.network

sealed class ApiResponse<out T>
data class ApiSuccessResponse<out T>(val body: T) : ApiResponse<T>()
data class ApiErrorResponse(
    val message: String, val isNetworkAvailable: Boolean = true
) : ApiResponse<Nothing>()

data class ApiSessionExpired(val message: String) : ApiResponse<Nothing>()


