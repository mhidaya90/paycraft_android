package com.paycraft.card.add.verify

import com.google.gson.annotations.SerializedName

class AddCardVerifyOtpRequest(
    @SerializedName("card_reference_number") val cardId: String,
    @SerializedName("otp") val otp: String,
    @SerializedName("txnId") val txnId: String
)