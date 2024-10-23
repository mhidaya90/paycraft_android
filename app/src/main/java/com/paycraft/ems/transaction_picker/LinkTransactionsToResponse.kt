package com.paycraft.ems.transaction_picker

import com.paycraft.base.BaseResponse
import com.paycraft.ems.reports.Report

class LinkTransactionsToResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: LinkTransactionsToRes?
) : BaseResponse(status, message, errors)

class LinkTransactionsToRes(val report: Report?)