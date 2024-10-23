package com.paycraft.ems.filter

import com.paycraft.ems.transactions.Filters

interface FilterPickerBottomSheetListener {
    fun onApplyFilter(list: List<Filters>)
}