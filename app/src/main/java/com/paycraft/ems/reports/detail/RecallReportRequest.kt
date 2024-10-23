package com.paycraft.ems.reports.detail

import com.google.gson.annotations.SerializedName

class RecallReportRequest(
    @SerializedName("report_id") val reportId: String
)