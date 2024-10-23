package com.paycraft.ems.report_expenses

interface ReportExpensesFragmentListener {
    fun onTransactionUnLined();
    fun canUnlink(): Boolean
}