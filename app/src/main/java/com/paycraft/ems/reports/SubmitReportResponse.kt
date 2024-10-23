package com.paycraft.ems.reports

import com.paycraft.base.BaseResponse

class SubmitReportResponse(
    status: Boolean?,
    message: String?,
    errors: String?
) : BaseResponse(status, message, errors)