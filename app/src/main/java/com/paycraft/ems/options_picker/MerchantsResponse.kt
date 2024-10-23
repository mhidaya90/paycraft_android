package com.paycraft.ems.options_picker

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class MerchantsResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    @SerializedName("response") val response: List<Merchant>?
) : BaseResponse(status, message, errors) {
}

class Merchant(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("code") val code: String?,
    @SerializedName("enable") val enable: Boolean?,
)
