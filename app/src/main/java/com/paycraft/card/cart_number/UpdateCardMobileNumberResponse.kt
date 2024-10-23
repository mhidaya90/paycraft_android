package com.paycraft.card.cart_number

import com.paycraft.base.BaseResponse

class UpdateCardMobileNumberResponse(
    status: Boolean?,
    message: String?,
    errors: String?
) : BaseResponse(status, message, errors)