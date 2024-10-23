package com.paycraft.ems.transactions

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.ems.RecordStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class RecordActivity : BaseActivity() {
    companion object {
        fun statusColors(
            context: Context,
            status: String,
            view: View,
            textView: TextView,
            indicatorView: View? = null
        ) {
            when (status) {
                RecordStatus.STATUS_UN_SUBMITTED,
                RecordStatus.STATUS_RECOVERED
                -> {
                    view.setBackgroundResource(R.drawable.bg_circle_saved)
                    textView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.recordColorSaved
                        )
                    )
                    indicatorView?.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.recordColorSaved
                        )
                    )
                }

                RecordStatus.STATUS_CLOSED,
                RecordStatus.STATUS_REIMBURSED,
                RecordStatus.STATUS_APPROVED,
                RecordStatus.STATUS_RECOVERED -> {
                    view.setBackgroundResource(R.drawable.bg_circle_done)
                    textView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.recordColorDone
                        )
                    )
                    indicatorView?.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.recordColorDone
                        )
                    )
                }

                RecordStatus.STATUS_PENDING_APPROVAL,
                RecordStatus.STATUS_PENDING_REIMBURSEMENT,
                RecordStatus.STATUS_PENDING_RECOVERY -> {
                    view.setBackgroundResource(R.drawable.bg_circle_pending)
                    textView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.recordColorPending
                        )
                    )
                    indicatorView?.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.recordColorPending
                        )
                    )
                }

                RecordStatus.STATUS_REJECTED,
                RecordStatus.STATUS_CANCELLED -> {
                    view.setBackgroundResource(R.drawable.bg_circle_rejected)
                    textView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.recordColorRejected
                        )
                    )
                    indicatorView?.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.recordColorRejected
                        )
                    )
                }

                else -> {
                    view.setBackgroundResource(R.drawable.bg_circle)
                    textView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.ash3
                        )
                    )
                    indicatorView?.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.ash3
                        )
                    )
                }
            }
        }
    }
}