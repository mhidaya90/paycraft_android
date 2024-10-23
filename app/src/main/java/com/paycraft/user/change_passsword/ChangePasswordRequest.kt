package com.paycraft.user.change_passsword

import com.google.gson.annotations.SerializedName

class ChangePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)