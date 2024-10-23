package com.paycraft.ems.transactions

import com.paycraft.base.BaseResponse

class TransactionFieldsResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: TransactionFieldsRes?
) : BaseResponse(status, message, errors)

class TransactionFieldsRes(
    val fields: List<TransactionField>?,
    val files: List<String>?
)

