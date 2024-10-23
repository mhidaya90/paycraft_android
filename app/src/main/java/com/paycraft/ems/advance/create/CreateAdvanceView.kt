package com.paycraft.ems.advance.create

import com.paycraft.base.BaseView
import com.paycraft.ems.advance.list.Advance

interface CreateAdvanceView : BaseView {
    fun onCreateAdvanceSuccess(advance: Advance)
}