package com.paycraft.card.cards.balances

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class TopUpResponse(
    @SerializedName("response") val response: TopUpResponseBody?,
    status: Boolean?, message: String?, errors: String?
) : BaseResponse(status, message, errors)

class TopUpResponseBody(@SerializedName("payment_gateway_url") val paymentGatewayUrl: String?)
