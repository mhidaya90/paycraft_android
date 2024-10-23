package com.paycraft.ems.comments

import com.google.gson.annotations.SerializedName

class CreateTransactionCommentRequest(
    @SerializedName("transaction_id") val transactionId: String,
    val body: String
)