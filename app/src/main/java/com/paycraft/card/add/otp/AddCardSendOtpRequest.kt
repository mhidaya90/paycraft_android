package com.paycraft.card.add.otp

import com.google.gson.annotations.SerializedName

val CARD_ACTIVATION = "CARDACTIVATION"
val NEW_CUSTOMER_REGISTRATION = "NEW_CUSTOMER_REGISTRATION"

class AddCardSendOtpRequest(
    @SerializedName("card_reference_number") val cardId: String,
    @SerializedName("action_type") val actionType: String
)