package com.paycraft.ems.comments

import com.paycraft.base.BaseResponse

class CommentsResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    val response: CommentsRes?
) : BaseResponse(status, message, errors)

class CommentsRes(val comments: List<Comment>?)