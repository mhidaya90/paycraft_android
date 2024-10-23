package com.paycraft.ems.transactions

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class CategoriesResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: CategoriesRes?
) : BaseResponse(status, message, errors)

class CategoriesRes(
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("total_records") val totalRecords: String
)