package com.paycraft.ems.history

import com.paycraft.base.BaseResponse

class HistoryResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: HistoryRes?
) : BaseResponse(status, message, errors)

class HistoryRes(val history: List<History>?)