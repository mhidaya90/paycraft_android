package com.paycraft.card.add.otp

import com.paycraft.base.BaseView

interface AddCarView : BaseView {
    fun onOtpSent(id: String)
}