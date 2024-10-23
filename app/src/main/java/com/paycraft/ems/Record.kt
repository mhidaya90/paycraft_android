package com.paycraft.ems

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.paycraft.ems.RecordStatus.Companion.STATUS_PENDING
import com.paycraft.ems.RecordStatus.Companion.STATUS_UN_SUBMITTED
import com.paycraft.ems.reports.detail.Violations
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.TransactionFile
import kotlinx.android.parcel.Parcelize

class RecordStatus {
    companion object {
        //report
        const val STATUS_UN_SUBMITTED = "unsubmitted"
        const val STATUS_PENDING_APPROVAL = "pending_approval";
        const val STATUS_APPROVED = "approved"
        const val STATUS_REJECTED = "rejected"
        const val STATUS_PARTIAL_APPROVED = "partial_approved"
        const val STATUS_REIMBURSED = "reimbursed"
        const val STATUS_PARTIAL_REIMBURSED = "partial_reimbursed"
        const val STATUS_PENDING_REIMBURSEMENT = "pending_reimbursement"

        //transaction
        const val STATUS_PENDING = "pending"
        const val STATUS_REPORTED = "reported"

        //trip
        const val STATUS_CANCELLED = "cancelled";
        const val STATUS_CLOSED = "closed";
        const val STATUS_SUBMITTED = "submitted";
        const val STATUS_COVERED = "covered";
        const val STATUS_PENDING_RECOVER = "pending_recover"

        //advance
        const val STATUS_PENDING_RECOVERY = "pending_recovery"
        const val STATUS_RECOVERED = "recovered"
    }
}

@Parcelize
open class Record : IRecord, Parcelable {
    @SerializedName("id")
    val id: String? = null

    @SerializedName("number")
    val number: String? = null

    @SerializedName("display_status")
    val displayStatus: String? = null

    @SerializedName("status")
    val status: String? = null

    @SerializedName("expense_errors")
    val expenseErrors: Violations? = null

    @SerializedName("recallable")
    val recallable: Boolean? = null

    @SerializedName("fields")
    val mFields: List<FieldOption>? = null

    @SerializedName("files")
    val mFiles: List<TransactionFile>? = null


    fun canEdit(): Boolean {
        return STATUS_UN_SUBMITTED == status
                || STATUS_PENDING == status

    }

    fun canDelete(): Boolean {
        return STATUS_UN_SUBMITTED == status || STATUS_PENDING == status
    }

    override fun displayStatus(): String {
        return (if (displayStatus?.isNotEmpty() == true)
            displayStatus
        else
            status) ?: "Un-Known"
    }

    override fun equals(other: Any?): Boolean {
        return id == ((other as? Record)?.id)
    }
}