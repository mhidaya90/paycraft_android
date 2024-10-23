package com.paycraft.network

import android.util.Log
import com.paycraft.base.BaseResponse
import retrofit2.Response


open class BaseRepo {
    private val TAG = "BaseRepo"
    val ERROR_MESSAGE = "Something went wrong!"
    val ERROR_OOPS = "OOps .. Something went wrong!"
    suspend fun <T> executeApi(call: suspend () -> Response<T>): ApiResponse<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful) {
                val body = response.body()
                if (null == body) {
                    ApiErrorResponse("No data in body error!")
                } else {
                    ApiSuccessResponse(body)
                }
            } else if (422 == response.code()) {
                ApiSessionExpired("Session expired!")
            } else {
                ApiErrorResponse(errorMessage(response))
            }
        } catch (throwable: Throwable) {
            Log.e(TAG, "executeApi: ", throwable)
            ApiErrorResponse(throwable.message ?: ERROR_OOPS)
        }
    }

    private fun errorMessage(t: Response<*>): String {
        var message = ERROR_MESSAGE
        try {
            if (500 == t.code() || 401 == t.code() || 404 == t.code()) {
                t.errorBody()?.string()?.let {
                    message = BaseResponse.fromJson(it)?.message ?: ERROR_MESSAGE
                }
            } else {
                message = ERROR_MESSAGE
            }
        } catch (e: Exception) {
            message = ERROR_MESSAGE
        }
        return message
    }
}