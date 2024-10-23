package com.paycraft.card.cards.settings

import com.google.gson.annotations.SerializedName

class SetUpRequest(
    @SerializedName("card_id") val cardId: String
)