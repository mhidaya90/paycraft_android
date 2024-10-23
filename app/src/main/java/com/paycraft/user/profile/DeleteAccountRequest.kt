package com.paycraft.user.profile

import com.google.gson.annotations.SerializedName

class DeleteAccountRequest(
    @SerializedName("id") val id: String,
    @SerializedName("status") val status: String = "deleted"
)