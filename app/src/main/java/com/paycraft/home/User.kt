package com.paycraft.home

import com.google.gson.annotations.SerializedName
import com.paycraft.card.cards.Card
import com.paycraft.ems.transactions.Transaction

class User(
    val id: String?,
    val name: String?,
    val email: String?,
    val has_expense_management: Boolean?,
    val unreported_expense_count: Int?,
    val unsubmitted_reports_count: Int?,
    @SerializedName("cost_center") val costCenter: String?,
    @SerializedName("project") val project: String?,
    @SerializedName("grade") val grade: String?,
    @SerializedName("designation") val designation: String?,
    @SerializedName("emp_id") val empId: String?,
    @SerializedName("primary_phone") val primaryPhone: String?,
    @SerializedName("secondary_phone") val secondaryPhone: String?,
    @SerializedName("reporting_manager") val reportingManager: String?,
    @SerializedName("unread_notification_count") val unreadNotificationCount: Int?,
    @SerializedName("roles") val roles: List<String>?,
    @SerializedName("cards") val cards: List<Card>?,
    @SerializedName("latest_transactions") val transactions: List<Transaction>?
) {
    fun isAgent(): Boolean {
        return roles?.contains("Agent") ?: false
    }

    fun noCards(): Boolean {
        return cards == null || cards?.isEmpty() == true
    }
}