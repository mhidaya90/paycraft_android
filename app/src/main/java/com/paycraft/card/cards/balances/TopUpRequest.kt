package com.paycraft.card.cards.balances

import com.google.gson.annotations.SerializedName

class TopUpRequest(
    @SerializedName("card_id") var cardId: String,
    @SerializedName("amount") val amount: String
)

