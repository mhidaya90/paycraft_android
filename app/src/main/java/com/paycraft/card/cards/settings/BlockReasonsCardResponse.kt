package com.paycraft.card.cards.settings

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class BlockReasonsCardResponse(
    status: Boolean?, message: String?,
    errors: String?,
    @SerializedName("response") val response: List<BlockReason>?
) : BaseResponse(status, message, errors)