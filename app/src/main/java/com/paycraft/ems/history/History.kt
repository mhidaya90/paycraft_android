package com.paycraft.ems.history

import com.google.gson.annotations.SerializedName

class History(
    @SerializedName("id") val id: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("prev_status") val prevStatus: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("created_at") val createdAt: String?,
)
