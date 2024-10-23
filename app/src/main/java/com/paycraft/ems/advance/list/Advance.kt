package com.paycraft.ems.advance.list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.paycraft.ems.Record
import kotlinx.android.parcel.Parcelize

@Parcelize
class Advance(
    @SerializedName("title") val title: String?,
    @SerializedName("amount") val amount: String?,
    val purpose: String?,
    @SerializedName("payment_method") val paymentMethod: String?,
    @SerializedName("merchant") val merchant: String?,
    @SerializedName("category_id") val categoryId: String?,
    val category: String?,
    @SerializedName("reference_no") val referenceNo: String?,
    @SerializedName("transaction_time") val transactionTime: String?,
    @SerializedName("category_name") val categoryName: String?,
    @SerializedName("advance_date") val advanceDate: String?,
    @SerializedName("created_at") val createdAt: String?,
    val description: String?,
) : Record(), Parcelable {
    val displayDate: String? = null
    var isSelected: Boolean? = false

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun displayAmount(): String {
        return "Rs. ${amount ?: 0}"
    }
}
