package com.paycraft.ems.trip.details

import com.paycraft.base.BaseResponse

class SubmitTripResponse(
    status: Boolean?,
    message: String?,
    errors: String?
) : BaseResponse(status, message, errors)

