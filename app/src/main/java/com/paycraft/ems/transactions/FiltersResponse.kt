package com.paycraft.ems.transactions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse
import kotlinx.android.parcel.Parcelize

class FiltersResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    @SerializedName("response") val response: List<Filters>?
) : BaseResponse(status, message, errors)

@Parcelize
class Filters(
    @SerializedName("title") val title: String?,
    @SerializedName("key") val key: String?,
    @SerializedName("filters", alternate = ["filter"]) val filters: List<Filter>?
) : Parcelable

@Parcelize
class Filter(
    @SerializedName("id") val status: String?,
    @SerializedName("name") val displayStatus: String?
) : Parcelable {
    var isSelected: Boolean = false
}
