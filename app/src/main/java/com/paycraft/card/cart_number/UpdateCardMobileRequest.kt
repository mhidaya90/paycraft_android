package com.paycraft.card.cart_number

import com.google.gson.annotations.SerializedName

class UpdateCardMobileRequest(
    @SerializedName("card_id") val cardId: String,
    @SerializedName("new_mobile_number") val mobile: String
)