package com.paycraft.ems.reports.create

import com.paycraft.base.BaseResponse
import com.paycraft.ems.reports.Report

class CreateReportResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: CreateReportRes?
) : BaseResponse(status, message, errors)

class CreateReportRes(
    val report: Report?
)