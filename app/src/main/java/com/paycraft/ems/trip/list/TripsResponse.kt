package com.paycraft.ems.trip.list

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse
import com.paycraft.ems.trip.create.Trip

class TripsResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: TripsRes?
) : BaseResponse(status, message, errors)

class TripsRes(
    @SerializedName("trips")
    val trips: List<Trip>?
)