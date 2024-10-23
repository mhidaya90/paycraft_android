package com.paycraft.base

import android.widget.EditText

interface BaseView {
    fun showToast(message: String)
    fun showStaticError(message: String)
    fun showProgress()
    fun dismissProgress()
    fun sessionExpired()
    fun dateTimeDialog(
        targetView: EditText,
        isDateEnabled: Boolean,
        isTimeEnabled: Boolean
    )
}