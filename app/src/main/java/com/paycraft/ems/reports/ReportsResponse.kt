package com.paycraft.ems.reports

import com.paycraft.base.BaseResponse

class ReportsResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: ReportsRes?
) : BaseResponse(status, message, errors) {
}

class ReportsRes(
    val reports: List<Report>?
)

