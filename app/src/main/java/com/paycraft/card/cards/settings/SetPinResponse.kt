package com.paycraft.card.cards.settings

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class SetPinResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: SetPinRes?
) : BaseResponse(status, message, errors)

class SetPinRes(@SerializedName("set_pin_url") val url: String?)