package com.paycraft.card.add.verify

import com.google.gson.annotations.SerializedName

class AddCardRegisterRequest(
    @SerializedName("card_reference_number") val cardId: String
)