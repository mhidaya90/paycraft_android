package com.paycraft.ems.transactions.detail

import com.paycraft.base.BaseView
import com.paycraft.ems.reports.Report
import com.paycraft.ems.transactions.Transaction

interface TransactionView : BaseView {
    fun onTransactionSuccess(transaction: Transaction)
    fun onUnSubmittedReportsSuccess(report: List<Report>)
    fun onCreateTransactionCommentSuccess()
    fun onDeleteTransactionSuccess()
    fun onCreateReportCommentSuccess()
    fun onTransactionLinedToReport()
}