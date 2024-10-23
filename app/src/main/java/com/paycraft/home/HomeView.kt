package com.paycraft.home

import com.paycraft.base.BaseView

interface HomeView : BaseView {
    fun onOtpSent(card: String, trxId: String)
}