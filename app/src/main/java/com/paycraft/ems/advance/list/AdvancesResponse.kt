package com.paycraft.ems.advance.list

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class AdvancesResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: AdvancesRes?
) : BaseResponse(status, message, errors)

class AdvancesRes(
    @SerializedName("advances")
    val advances: List<Advance>?
)