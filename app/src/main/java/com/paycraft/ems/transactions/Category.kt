package com.paycraft.ems.transactions

import com.google.gson.annotations.SerializedName

class Category(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("status") val status: Boolean?,
    @SerializedName("is_bill_mandatory") val isBillMandatory: Boolean?,
    @SerializedName("is_document_mandatory") val isDocumentMandatory: Boolean?,
    @SerializedName("is_submited_feature_bill") val isSubmitedFeatureBill: Boolean?,
    @SerializedName("company_id") val companyId: Boolean?,
    @SerializedName("is_enable") val isEnable: Boolean?,
)