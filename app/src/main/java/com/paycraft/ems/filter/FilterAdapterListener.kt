package com.paycraft.ems.filter

import com.paycraft.ems.transactions.Filter

interface FilterAdapterListener {
    fun onRemoveFilter(filter: Filter)
}