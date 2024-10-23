package com.paycraft.ems.advance.detail

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse
import com.paycraft.ems.advance.list.Advance

class AdvanceResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: AdvanceRes?
) : BaseResponse(status, message, errors)

class AdvanceRes(
    @SerializedName(value = "advances")
    val advance: List<Advance>?
)