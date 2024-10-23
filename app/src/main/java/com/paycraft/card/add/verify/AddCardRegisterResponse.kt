package com.paycraft.card.add.verify

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class AddCardRegisterResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    @SerializedName("response") val response: String
) : BaseResponse(status, message, errors) {
}