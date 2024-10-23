package com.paycraft.file_picker

import com.paycraft.ems.transactions.TransactionFile

interface FilePickerBottomSheetFragmentListener {
    fun onFileSelected(file: TransactionFile)
}