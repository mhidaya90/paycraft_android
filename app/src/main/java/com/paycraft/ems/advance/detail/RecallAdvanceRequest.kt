package com.paycraft.ems.advance.detail

import com.google.gson.annotations.SerializedName

class RecallAdvanceRequest(
    @SerializedName("advance_id") val advanceId: String
)