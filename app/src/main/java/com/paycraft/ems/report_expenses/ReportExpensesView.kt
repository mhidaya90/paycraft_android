package com.paycraft.ems.report_expenses

import com.paycraft.base.BaseView
import com.paycraft.ems.transactions.Transaction

interface ReportExpensesView : BaseView {
    fun onReportTransactionsSuccess(transactions: List<Transaction>)
    fun onUnLinkTransactionsSuccess()
}