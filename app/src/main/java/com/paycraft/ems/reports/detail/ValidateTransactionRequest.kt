package com.paycraft.ems.reports.detail

import com.google.gson.annotations.SerializedName

class ValidateTransactionRequest(
    @SerializedName("report_id") val reportId: String?,
    @SerializedName("transaction_ids") val transactionIds: List<String>
)