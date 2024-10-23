package com.paycraft.card.add.otp

import com.paycraft.base.BaseResponse

class AddCardSendOtpResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: CardSendOtpRes?
) :
    BaseResponse(status, message, errors)

class CardSendOtpRes(val txnId: String?)