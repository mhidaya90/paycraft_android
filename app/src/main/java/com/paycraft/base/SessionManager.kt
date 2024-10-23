package com.paycraft.base

class SessionManager {
    val SP_FCM_TOKEN = "fcmToken"
    val SP_HEADER = "header"
    val SP_EMP_ID = "emp_id"
    val SP_NAME = "name"
    val SP_EMAIL = "email"
    val SP_CURRENCY = "currency"
    val SP_HAS_EXPENSE_MANAGEMENT = "has_expense_management"

    companion object {
        private val ourInstance = SessionManager()
        fun instance(): SessionManager {
            return ourInstance
        }
    }

    fun isLoggedIn(): Boolean {
        return SpManager.instance().preference()?.getString(SP_HEADER, "")?.isNotEmpty() ?: false
    }

    fun getFcmToken(): String {
        return SpManager.instance().preference()?.getString(SP_FCM_TOKEN, "") ?: ""
    }

    fun setFcmToken(token: String) {
        SpManager.instance().preference()?.edit()?.putString(SP_FCM_TOKEN, token)?.apply()
    }

    fun getHeader(): String {
        return SpManager.instance().preference()?.getString(SP_HEADER, "") ?: ""
    }

    fun setHeader(token: String) {
        SpManager.instance().preference()?.edit()?.putString(SP_HEADER, token)?.apply()
    }

    fun getEmpId(): String {
        return SpManager.instance().preference()?.getString(SP_EMP_ID, "") ?: ""
    }

    fun setEmpId(id: String) {
        SpManager.instance().preference()?.edit()?.putString(SP_EMP_ID, id)?.apply()
    }

    fun getName(): String {
        return SpManager.instance().preference()?.getString(SP_NAME, "") ?: ""
    }

    fun setName(token: String) {
        SpManager.instance().preference()?.edit()?.putString(SP_NAME, token)?.apply()
    }

    fun getEmail(): String {
        return SpManager.instance().preference()?.getString(SP_EMAIL, "") ?: ""
    }

    fun setEmail(token: String) {
        SpManager.instance().preference()?.edit()?.putString(SP_EMAIL, token)?.apply()
    }

    fun getCurrency(): String {
        return SpManager.instance().preference()?.getString(SP_CURRENCY, "") ?: ""
    }

    fun setCurrency(c: String) {
        SpManager.instance().preference()?.edit()?.putString(
            SP_CURRENCY,
           c
        )?.apply()
    }

    fun clear() {
        SpManager.instance().clear()
    }

    fun hasExpenseManagement(): Boolean {
        return SpManager.instance().preference()?.getBoolean(SP_HAS_EXPENSE_MANAGEMENT, false)
            ?: false
    }

    fun setHasExpenseManagement(hem: Boolean) {
        SpManager.instance().preference()?.edit()?.putBoolean(SP_HAS_EXPENSE_MANAGEMENT, hem)
            ?.apply()
    }
}