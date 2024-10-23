package com.paycraft.notifications

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class NotificationResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    @SerializedName("response") val response: NotificationRes
) : BaseResponse(status, message, errors)

class NotificationRes(@SerializedName("notification") val notifications: List<PaycraftNotification>?)

class PaycraftNotification(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("company_id") val companyId: String?,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("employee_id") val employeeId: String?,
    @SerializedName("seen") var seen: Boolean?,
    @SerializedName("created_at") var createdAt: String?,
    @SerializedName("data") val data: RecordData?,
) {
    fun markAsSeen() {
        seen = true
    }
}

class RecordData(
    @SerializedName("id") val id: String?,
    @SerializedName("page") val page: String?

)