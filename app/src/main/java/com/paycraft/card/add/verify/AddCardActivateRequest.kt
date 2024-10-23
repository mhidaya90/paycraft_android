package com.paycraft.card.add.verify

import com.google.gson.annotations.SerializedName

class AddCardActivateRequest(
    @SerializedName("card_reference_number") val cardId: String
)


