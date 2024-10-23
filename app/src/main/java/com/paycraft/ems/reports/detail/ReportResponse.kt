package com.paycraft.ems.reports.detail

import com.paycraft.base.BaseResponse
import com.paycraft.ems.reports.Report

class ReportResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: ReportRes?
) : BaseResponse(status, message, errors)

class ReportRes(val report: Report?)