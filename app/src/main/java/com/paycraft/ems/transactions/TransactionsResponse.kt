package com.paycraft.ems.transactions

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class TransactionsResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: TransactionsRes?
) : BaseResponse(status, message, errors)

class TransactionsRes(
    @SerializedName(value = "transactions", alternate = ["advances"])
    val transactions: List<Transaction>?
)