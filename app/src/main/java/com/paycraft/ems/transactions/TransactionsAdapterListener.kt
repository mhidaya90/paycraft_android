package com.paycraft.ems.transactions

import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.reports.Report
import com.paycraft.ems.trip.create.Trip

interface TransactionsAdapterListener {
    fun onTransactionClicked(transaction: Transaction) {

    }

    fun onReportClicked(report: Report) {

    }

    fun isSelected(transaction: Transaction): Boolean {
        return false
    }

    fun onTripClicked(advance: Trip) {

    }

    fun isSelected(advance: Trip): Boolean {
        return false
    }

    fun onAdvanceClicked(advance: Advance) {

    }

    fun isSelected(advance: Advance): Boolean {
        return false
    }
}