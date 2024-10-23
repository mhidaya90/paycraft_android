package com.paycraft.ems.transactions

import com.paycraft.base.BaseResponse

class CreateTransactionResponse(
    status: Boolean,
    message: String?,
    errors: String?,
    val response: ResCreateTransaction?
) : BaseResponse(status, message, errors)

class ResCreateTransaction(
    val transaction: Transaction?
)