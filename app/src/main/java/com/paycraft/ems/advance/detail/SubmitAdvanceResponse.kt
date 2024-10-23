package com.paycraft.ems.advance.detail

import com.paycraft.base.BaseResponse

class SubmitAdvanceResponse(
    status: Boolean?,
    message: String?,
    errors: String?
) : BaseResponse(status, message, errors)