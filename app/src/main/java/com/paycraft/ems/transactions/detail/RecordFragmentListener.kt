package com.paycraft.ems.transactions.detail

import com.paycraft.ems.Record

interface RecordFragmentListener {
    fun getRecord(): Record

    fun getType(): String
}