package com.paycraft.ems.comments

import com.google.gson.annotations.SerializedName

class CreateAdvanceCommentRequest(
    @SerializedName("advance_id") val transactionId: String,
    val body: String
)