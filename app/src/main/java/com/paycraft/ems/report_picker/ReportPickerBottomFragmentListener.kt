package com.paycraft.ems.report_picker

import com.paycraft.ems.reports.Report

interface ReportPickerBottomFragmentListener {
    fun onReportSelected(report: Report)
}