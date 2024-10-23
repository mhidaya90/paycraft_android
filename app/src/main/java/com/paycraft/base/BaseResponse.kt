package com.paycraft.base

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

open class BaseResponse(
    val status: Boolean?,
    val message: String?,
    val errors: String?,
    @SerializedName("total_records") val totalRecords: Int? = 0
) {
    companion object {
        fun fromJson(json: String): BaseResponse? {
            return Gson().fromJson(json, BaseResponse::class.java)
        }
    }

    fun success(): Boolean = status ?: false
    open fun message(): String {
        if (!errors.isNullOrEmpty())
            return errors
        if (!message.isNullOrEmpty())
            return message
        return "Something went wrong!"
    }

    fun total(): Int = totalRecords ?: 0
}