package com.paycraft.ems.advance.detail

import com.google.gson.annotations.SerializedName

class SubmitAdvanceRequest (
    @SerializedName("advance_id") val id: String
)