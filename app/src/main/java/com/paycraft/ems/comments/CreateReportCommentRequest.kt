package com.paycraft.ems.comments

import com.google.gson.annotations.SerializedName

class CreateReportCommentRequest(
    @SerializedName("report_id") val reportId: String,
    val body: String
)