package com.paycraft.ems.advance.create

import com.paycraft.base.BaseResponse
import com.paycraft.ems.advance.list.Advance

class CreateAdvanceResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: CreateAdvanceRes?
) : BaseResponse(status, message, errors)

class CreateAdvanceRes(
    val advance: Advance?
)