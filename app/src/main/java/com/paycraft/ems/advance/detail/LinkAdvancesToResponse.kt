package com.paycraft.ems.advance.detail

import com.paycraft.base.BaseResponse

class LinkAdvancesToResponse(
    status: Boolean?,
    message: String?,
    errors: String?
) : BaseResponse(status, message, errors)