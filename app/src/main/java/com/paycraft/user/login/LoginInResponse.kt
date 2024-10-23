package com.paycraft.user.login

import com.google.gson.annotations.SerializedName
import com.paycraft.base.BaseResponse

class LoginInResponse(
    status: Boolean?,
    message: String?,
    val user: String?,
    errors: String?,
    @SerializedName("auth_token") val authToken: String?,
    @SerializedName("employee_id") val employeeId: String?,
    @SerializedName("base_currency") val baseCurrency: BaseCurrency?
) : BaseResponse(status, message, errors)

class BaseCurrency(
    @SerializedName("id") val id: String?,
    @SerializedName("base_currency_id") val baseCurrencyId: String?,
    @SerializedName("base_currency") val baseCurrency: String?,
    @SerializedName("iso_code") val isoCode: String?,
    @SerializedName("symbol_code") val symbolCode: String?,
    @SerializedName("symbol") val symbol: String?
)