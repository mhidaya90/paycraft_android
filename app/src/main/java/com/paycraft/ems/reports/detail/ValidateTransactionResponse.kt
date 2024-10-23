package com.paycraft.ems.reports.detail

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse
import kotlinx.android.parcel.Parcelize

class ValidateTransactionResponse(
    status: Boolean?,
    message: String?,
    errors: String?,
    @SerializedName("response") val response: Violations?
) : BaseResponse(status, message, errors)

class Violations(
    @SerializedName("transactions") val transaction: List<PolicyInfo>?,
    @SerializedName("report") val report: List<PolicyInfo>?
) {
    fun getViolationList(): List<DisplayViolation> {
        val displayViolation = mutableListOf<DisplayViolation>()
        transaction?.forEach { t ->
            t.policies?.forEach { p ->
                p.errors?.forEach { e ->
                    displayViolation.add(
                        DisplayViolation(t.id, t.number, p.isWarning, p.isBlocked, e)
                    )
                }
            }
        }
        report?.forEach { t ->
            t.policies?.forEach { p ->
                p.errors?.forEach { e ->
                    displayViolation.add(
                        DisplayViolation(
                            t.id,
                            t.number,
                            p.isWarning,
                            p.isBlocked,
                            e
                        )
                    )
                }
            }
        }
        return displayViolation
    }
}

class PolicyInfo(
    @SerializedName("transaction_id", alternate = ["report_id"]) val id: String?,
    @SerializedName("transaction_number", alternate = ["report_number"]) val number: String?,
    @SerializedName("policies") val policies: List<Policy>?,
)

class Policy(
    @SerializedName("policy_id") val policyId: String?,
    @SerializedName("policy_name") val policyName: String?,
    @SerializedName("is_blocked") val isBlocked: Boolean?,
    @SerializedName("is_warning") val isWarning: Boolean?,
    @SerializedName("errors") val errors: List<String>?
)

@Parcelize
class DisplayViolation(
    val id: String?,
    val number: String?,
    val isWarning: Boolean?,
    val isBlocked: Boolean?,
    val error: String?
) : Parcelable

