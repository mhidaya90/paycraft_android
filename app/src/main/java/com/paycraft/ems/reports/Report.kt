package com.paycraft.ems.reports

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.paycraft.base.toRupee
import com.paycraft.ems.Record
import com.paycraft.ems.transactions.Transaction
import kotlinx.android.parcel.Parcelize

@Parcelize
class Report(
    @SerializedName("report_number") val reportNumber: String?,
    @SerializedName("title") val reportTitle: String?,
    @SerializedName("from_date") val fromDate: String?,
    @SerializedName("to_date") val toDate: String?,
    @SerializedName("total_amount") val totalAmount: String?,
    val transactions: List<Transaction>?,
    val description: String?
) : Record(), Parcelable {

    fun displayAmount(): String {
        return totalAmount?.toRupee() ?: "0".toRupee()
    }
}
