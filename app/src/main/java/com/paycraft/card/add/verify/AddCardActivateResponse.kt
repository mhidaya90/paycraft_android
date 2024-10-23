package com.paycraft.card.add.verify

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

const val RESPONSE_CMS149 = "cms149"
const val RESPONSE_00 = "00"
const val RESPONSE_200 = "200"

class AddCardActivateResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    @SerializedName("response") val response: AddCardActivateRes?
) : BaseResponse(status, message, errors) {
}

class AddCardActivateRes(
    @SerializedName("code", alternate = ["responseCode"]) val code: String?,
    @SerializedName("message", alternate = ["responseMessage"]) val message: String?,
    @SerializedName("url") val url: String?
)
