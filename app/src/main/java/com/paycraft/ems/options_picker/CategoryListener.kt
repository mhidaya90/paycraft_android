package com.paycraft.ems.options_picker

import com.paycraft.ems.transactions.Category

interface CategoryListener {
    fun onOptionSelected(
        category: Category?,
    )
}