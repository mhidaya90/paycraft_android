package com.paycraft.ems.transaction_picker

import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.trip.create.Trip

interface RecordPickerBottomFragmentListener {
    fun onTransactionsSelected(transactions:List<Transaction>)
    fun onTripsSelected(trips:List<Trip>)
    fun onAdvancesSelected(trips:List<Advance>)
}