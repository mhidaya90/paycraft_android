package com.paycraft.ems

import com.paycraft.ems.transaction_picker.LinkTransactionsToRequest

interface PolicyViolationsBottomSheetFragmentListener {
    fun continueWithWarnings(linkTransactionsToRequest: LinkTransactionsToRequest? = null)
}