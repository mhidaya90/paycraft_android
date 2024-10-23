package com.paycraft.ems.transactions

import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
class TransactionFile(
    val localFile: File? = null,
    val mimeType: String? = "",
    val extension: String? = "",
    @SerializedName("file") var base64Data: String? = "",
    val id: String? = null,
) : Parcelable {
    fun isPdg(): Boolean {
        return mimeType?.contains("pdf") ?: false
    }

    fun base64Data(): String {
        Log.d("TransactionFile", "base64Data: " + base64Data?.length)
        return base64Data?.split(",")?.last() ?: ""
    }

    override fun hashCode(): Int {
        return (if (!id.isNullOrBlank()) id else localFile?.path.toString()).hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TransactionFile) return false

        if (localFile != other.localFile) return false
        if (mimeType != other.mimeType) return false
        if (extension != other.extension) return false
        if (base64Data != other.base64Data) return false
        if (id != other.id) return false

        return true
    }
}
