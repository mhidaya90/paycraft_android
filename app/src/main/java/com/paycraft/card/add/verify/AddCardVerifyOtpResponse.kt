package com.paycraft.card.add.verify

import com.paycraft.base.BaseResponse

class AddCardVerifyOtpResponse(
    status: Boolean?,
    message: String?,
    errors: String?
) : BaseResponse(
    status, message, errors
)