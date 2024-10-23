package com.paycraft.user.signup

import com.google.gson.annotations.SerializedName

class SignUpRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("primary_phone") val phone: String,
    @SerializedName("password") val password: String
)