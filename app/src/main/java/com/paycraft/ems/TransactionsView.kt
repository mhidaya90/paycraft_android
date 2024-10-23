package com.paycraft.ems

import com.paycraft.base.BaseView

interface TransactionsView : BaseView {
    fun onDeleteTransactionSuccess()
    fun onDeleteReportSuccess()
    fun onDeleteTripSuccess()
    fun onDeleteAdvanceSuccess()
    fun onUnLinkTransactionsSuccess()
    fun onUnLinkTripSuccess()
    fun onUnLinkAdvanceSuccess()
}