package com.paycraft.ems.transactions

import com.paycraft.ems.advance.list.Advance

interface AdvancesAdapterListener {
    fun onTransactionClicked(advance: Advance)
    fun isSelected(advance: Advance): Boolean
}