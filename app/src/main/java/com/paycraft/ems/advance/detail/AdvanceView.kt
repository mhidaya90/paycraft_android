package com.paycraft.ems.advance.detail

import com.paycraft.base.BaseView
import com.paycraft.ems.advance.list.Advance

interface AdvanceView : BaseView {
    fun onAdvanceSuccess(advance: Advance)
    fun onAdvanceSubmittedSuccess()
    fun onAdvanceLinkingSuccess()
    fun onDeleteAdvanceSuccess()
    fun onCreateAdvanceCommentSuccess()
    fun onRecallAdvanceSuccess()
}