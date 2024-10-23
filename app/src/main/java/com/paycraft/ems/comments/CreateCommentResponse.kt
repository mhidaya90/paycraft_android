package com.paycraft.ems.comments

import com.paycraft.base.BaseResponse

class CreateCommentResponse(
    status: Boolean?,
    message: String?,
    errors: String?
) : BaseResponse(status, message, errors)