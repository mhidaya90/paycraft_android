package com.paycraft.card.cards.settings

import com.paycraft.base.BaseResponse

class BlockCardResponse(
    status: Boolean?,
    message: String?,
    errors: String?
) : BaseResponse(status, message, errors)