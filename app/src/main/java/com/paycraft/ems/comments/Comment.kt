package com.paycraft.ems.comments

import com.google.gson.annotations.SerializedName

class Comment(
    val id: String?,
    val body: String?,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("created_at") val createdAt: String?
)

