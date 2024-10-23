package com.paycraft.card.cards.settings

import com.paycraft.base.BaseView

interface CardSettingsView : BaseView {
    fun onBlockCard()
    fun onSetPinSuccess(url: String)
}