package com.paycraft.user.forgot

import com.google.gson.annotations.SerializedName

class ForgotPasswordRequest(@SerializedName("email") val email: String)