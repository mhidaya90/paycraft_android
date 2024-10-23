package com.paycraft.ems.trip.details

import com.google.gson.annotations.SerializedName

class SubmitTripRequest (
    @SerializedName("trip_id") val id: String
)