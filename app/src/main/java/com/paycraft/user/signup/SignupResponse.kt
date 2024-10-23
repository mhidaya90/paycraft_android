package com.paycraft.user.signup

import com.paycraft.base.BaseResponse

class SignupResponse(
    status: Boolean?,
    message: String?,
    errors: String?
) : BaseResponse(status, message, errors)