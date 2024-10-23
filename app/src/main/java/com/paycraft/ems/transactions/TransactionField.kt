package com.paycraft.ems.transactions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

const val COLUMN_TYPE_DATE = "date"
const val COLUMN_TYPE_DATE_TIME = "datetime"
const val COLUMN_TYPE_STRING = "string"
const val COLUMN_TYPE_INTEGER = "integer"
const val COLUMN_TYPE_DROP_DOWN = "dropdown"
const val COLUMN_TYPE_CHECK_BOX = "checkbox"

@Parcelize
class TransactionField(
    @SerializedName("field_id") var fieldId: String?,
    @SerializedName("field_name") var fieldName: String?,
    @SerializedName("display_name") var displayName: String?,
    @SerializedName("field_type") var fieldType: String?,
    @SerializedName("is_required") var isRequired: Boolean?,
    @SerializedName("static") var static: Boolean?,
    @SerializedName("default_values") var options: List<FieldOption>?,
    @SerializedName("selected") var selected: FieldOption?
) : Parcelable {
    fun isStatic(): Boolean {
        return static ?: false
    }
    fun isPurpose(): Boolean {
        return displayName?.lowercase() == "purpose"
    }
}

@Parcelize
class FieldOption(val id: String?, val value: String?, val key: String? = null) : Parcelable