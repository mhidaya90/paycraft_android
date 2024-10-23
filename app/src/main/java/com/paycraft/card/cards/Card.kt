package com.paycraft.card.cards

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.paycraft.base.toRupee
import kotlinx.android.parcel.Parcelize

const val ACTIVATED = "Activated"
const val PERMANENT_HOTLIST = "Permanent Hotlist"
const val TEMPORARY_HOTLIST = "Temporary Hotlist"
const val ISSUED_NOT_ACTIVE = "Issued not active"

@Parcelize
class Card(
    @SerializedName("id") val id: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("card_reference_no") val cardReferenceNo: String?,
    @SerializedName("card_number") val cardNumber: String?,
    @SerializedName("balance") val balance: String?,
    @SerializedName("expense_wallet") val expenseWallet: Int?,
    @SerializedName("reimburse_wallet") val reimburseWallet: Int?,
    @SerializedName("employee_details") val employeeDetails: EmployeeDetails?,
    @SerializedName("cvv") val cvv: String?,
    @SerializedName("validity") val validity: String?,
    @SerializedName("company_name") val companyName: String?,
    @SerializedName("data") val walletData: WalletData?,
    @SerializedName("wallets") val wallets: List<Wallet>?
) : Parcelable {
    fun isHotListed(): Boolean {
        return status?.lowercase()?.contains("permanent hotlist") ?: false
    }

    fun getFirstFour(): String {
        return walletData?.maskCardNumber?.split(" ")?.firstOrNull() ?: "XXXX"
    }

    fun getLastFour(): String {
        return walletData?.maskCardNumber?.split(" ")?.lastOrNull() ?: "XXXX"
    }

    fun isActive(): Boolean {
        return ACTIVATED.lowercase() == status?.lowercase()

    }

    /**
     * Temporary Hotlisted Card
     */
    fun isThl(): Boolean {
        return TEMPORARY_HOTLIST.lowercase() == status?.lowercase()
    }

    /**
     * In active cards
     */
    fun isInActive(): Boolean {
        return ISSUED_NOT_ACTIVE.lowercase() == status?.lowercase()
    }


    fun isIssuedNotActive(): Boolean {
        return "Issued not active".lowercase() == status?.lowercase()
    }

    fun displayBalance(): String {
        return walletData?.cardBalance?.toRupee() ?: "0".toRupee()
    }
}

@Parcelize
class EmployeeDetails(
    @SerializedName("employee_id") val employeeId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?
) : Parcelable

@Parcelize
class WalletData(
    @SerializedName("maskCardNumber") val maskCardNumber: String?,
    @SerializedName("cardBalance") val cardBalance: String?
) : Parcelable

@Parcelize
class Wallet(
    @SerializedName("id") val id: String?,
    @SerializedName("wallet_name") val walletName: String?,
    @SerializedName("balance") val balance: String?,
    @SerializedName("wallet_identifier") val walletIdentifier: String?
) : Parcelable {
    fun displayAmount(): String {
        return balance?.toRupee() ?: "0".toRupee()
    }
}