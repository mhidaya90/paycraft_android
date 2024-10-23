package com.paycraft.ems.reports.create

import com.paycraft.base.BaseView
import com.paycraft.ems.reports.Report

interface CreateReportView : BaseView {
    fun onCreateReportSuccess(report: Report)
}