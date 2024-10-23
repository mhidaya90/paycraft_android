package com.paycraft.ems.trip.details

import com.google.gson.annotations.SerializedName

class RecallTripRequest(
    @SerializedName("trip_id") val tripId: String
)