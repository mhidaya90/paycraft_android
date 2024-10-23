package com.paycraft.home

import com.paycraft.base.BaseResponse

class ProfileResponse(
    status: Boolean,
    message: String?,
    errors: String?,
    val response: ProfileRes?
) : BaseResponse(status, message, errors)

class ProfileRes(
    val profile: User?
)