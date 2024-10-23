package com.paycraft.card.cards

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class CardsResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    @SerializedName("response") val response: CardsRes?
) : BaseResponse(status, message, errors)

class CardsRes(@SerializedName("cards") val cards: List<Card>?)