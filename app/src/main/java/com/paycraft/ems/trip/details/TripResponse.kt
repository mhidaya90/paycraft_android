package com.paycraft.ems.trip.details

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse
import com.paycraft.ems.trip.create.Trip

class TripResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: TripRes?
) : BaseResponse(status, message, errors)

class TripRes(
    @SerializedName(value = "trips")
    val trips: List<Trip>?
)