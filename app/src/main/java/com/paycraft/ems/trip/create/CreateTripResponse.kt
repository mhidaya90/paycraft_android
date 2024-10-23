package com.paycraft.ems.trip.create

import com.paycraft.base.BaseResponse

class CreateTripResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: CreateTripRes?
) : BaseResponse(status, message, errors)

class CreateTripRes(
    val trip: Trip?
)