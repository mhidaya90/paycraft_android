package com.paycraft.card.add.verify

import com.paycraft.base.BaseView

interface AddCardVerifyOtpView : BaseView {
    fun onActivateCard(res: AddCardActivateRes)
    fun apiBefore()
    fun apiAfter()
}