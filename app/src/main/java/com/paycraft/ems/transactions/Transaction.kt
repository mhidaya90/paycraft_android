package com.paycraft.ems.transactions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.paycraft.base.toRupee
import com.paycraft.ems.Record
import kotlinx.android.parcel.Parcelize

class TransactionFields {
    companion object {
        const val TRANSACTION_FILED_AMOUNT = "amount_cents"
        const val TRANSACTION_FILED_DATE = "transaction_date"
        const val TRANSACTION_FILED_MERCHANT = "transaction_merchant_id"
        const val TRANSACTION_FILED_CATEGORY_ID = "category_id"
        const val TRANSACTION_FILED_CLAIM_REIMBURSABLE_ID = "claim_reimbursement"
    }
}

@Parcelize
class Transaction(
    @SerializedName("amount") val amount: String?,
    @SerializedName("transaction_date") val dateAndTime: String?,
    @SerializedName("purpose") val purpose: String?,
    @SerializedName("payment_method") val paymentMethod: String?,
    @SerializedName("merchant_name") val merchant: String?,
    @SerializedName("category_id") val categoryId: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("reference_no") val referenceNo: String?,
    @SerializedName("transaction_time") val transactionTime: String?,
    @SerializedName("category_name") val categoryName: String?,
    @SerializedName("description") val description: String?,
) : Record(), Parcelable {

    @SerializedName("data")
    val data: CardTransactionData? = null

    @SerializedName("report_id")
    val reportId: String? = null

    @SerializedName("wallet_name")
    val walletName: String? = null

    @SerializedName("card_id")
    val cardId: String? = null

    val displayDate: String? = null
    var isSelected: Boolean? = false
    var channelType: String? = ""

    @SerializedName("transaction_type")
    val transactionType: String? = null

    fun isCreditTransaction(): Boolean {
        return "credit" == transactionType || "reversal" == transactionType
    }

    fun isCardTransaction(): Boolean {
        return !cardId.isNullOrEmpty()
    }

    fun displayAmount(): String {
        return amount?.toRupee() ?: "0".toRupee()
    }
}

class CardTransactionData(
    @SerializedName("txnType") val txnType: String?
)