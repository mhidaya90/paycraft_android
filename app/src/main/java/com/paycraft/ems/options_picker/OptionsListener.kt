package com.paycraft.ems.options_picker

import com.paycraft.ems.transactions.FieldOption

interface OptionsListener {
    fun onOptionSelected(type: String, fieldOption: FieldOption)
}