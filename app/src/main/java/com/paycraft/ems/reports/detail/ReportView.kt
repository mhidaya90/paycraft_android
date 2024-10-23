package com.paycraft.ems.reports.detail

import com.paycraft.base.BaseView
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.reports.Report
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.trip.create.Trip

interface ReportView : BaseView {
    fun reportSuccess(report: Report)
    fun onTransactionSuccess(transaction: Transaction, rId: String)
    fun onTransactionsLinkedSuccess(report: Report)
    fun onCreateReportCommentSuccess()
    fun onDeleteReportSuccess()
    fun onRecallReportSuccess()
    fun onReportSubmittedSuccess()
    fun onTripLinkingSuccess(trips: List<Trip>)
    fun onAdvanceLinkingSuccess(advances: List<Advance>)
}