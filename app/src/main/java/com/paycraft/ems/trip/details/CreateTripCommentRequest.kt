package com.paycraft.ems.trip.details

import com.google.gson.annotations.SerializedName

class CreateTripCommentRequest(
    @SerializedName("trip_id") val transactionId: String,
    val body: String
)