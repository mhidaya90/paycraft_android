package com.paycraft.base

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paycraft.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseBottomSheetDialogFragment : BottomSheetDialogFragment(), BaseView {
    private var mBaseView: BaseView? = null

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mBaseView = activity as? BaseView?
        return BottomSheetDialog(
            requireContext(),
            theme
        ) // this doesn't call onViewCreated for fragments extending this
    }

    override fun showToast(message: String) {
        mBaseView!!.showToast(message)
    }

    override fun showStaticError(message: String) {
    }

    override fun showProgress() {
        mBaseView!!.showProgress()
    }

    override fun dismissProgress() {
        mBaseView!!.dismissProgress()
    }

    override fun sessionExpired() {
        mBaseView!!.sessionExpired()
    }

    override fun dateTimeDialog(
        targetView: EditText,
        isDateEnabled: Boolean,
        isTimeEnabled: Boolean
    ) {
        mBaseView!!.dateTimeDialog(targetView, isDateEnabled, isTimeEnabled)
    }
}