package com.paycraft.ems.options_picker

import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.TransactionField

interface CustomFieldsOptionsListener {
    fun onOptionSelected(
        transactionField: TransactionField,
        fieldOption: FieldOption
    )
}