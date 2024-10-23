package com.paycraft.card.cards

import com.paycraft.base.BaseView

interface CardsView : BaseView {
    fun onOtpSent(trxId: String)
    fun onTopUp(url: String)
}