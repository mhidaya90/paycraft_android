package com.paycraft.ems.report_expenses

import com.paycraft.base.BaseResponse
import com.paycraft.ems.transactions.Transaction

class TransactionResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: TransactionRes?
) : BaseResponse(status, message, errors)

class TransactionRes(val transaction: Transaction?)