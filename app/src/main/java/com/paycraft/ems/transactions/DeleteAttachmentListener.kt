package com.paycraft.ems.transactions

interface DeleteAttachmentListener {
    fun onDeleteFile(file: TransactionFile)
}