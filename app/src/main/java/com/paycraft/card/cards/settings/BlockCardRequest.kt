package com.paycraft.card.cards.settings

import com.google.gson.annotations.SerializedName

class BlockCardRequest(
    @SerializedName("card_id") val cardId: String,
    @SerializedName("reason") val reason: String
)