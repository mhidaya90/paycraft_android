package com.paycraft.card.cards.settings

import com.google.gson.annotations.SerializedName

class BlockReason(
    @SerializedName("instrumentType") val instrumentType: String,
    @SerializedName("reasons") val reasons: String,
    @SerializedName("reasonCode") val reasonCode: String
)