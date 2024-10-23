package com.paycraft.ems.transactions.create

import com.paycraft.base.BaseView
import com.paycraft.ems.transactions.Transaction

interface CreateTransactionView : BaseView {
    fun onCreateTransactionSuccess(transaction: Transaction)
}