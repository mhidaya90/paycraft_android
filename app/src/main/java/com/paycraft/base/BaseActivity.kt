package com.paycraft.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.paycraft.BuildConfig
import com.paycraft.home.LaunchActivity
import com.paycraft.R
import java.util.Calendar


open class BaseActivity : AppCompatActivity(), BaseView {
    companion object {
        const val E_REFRESH_PREVIOUS_SCREEN = "refreshPreviousScreen"
        const val RC_REFRESH_PREVIOUS_SCREEN = 100
    }

    private var progressBar: ProgressBar? = null
    private var mProgressTaskCount = 0
    protected var refreshPreviousScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BuildConfig.DEBUG)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
    }

    fun configToolbar(
        toolbar: Toolbar,
        titleTextView: TextView,
        title: String,
        backNavigation: Boolean = true,
        isDark: Boolean = false
    ) {
        setSupportActionBar(toolbar)
        titleTextView.text = title
        if (isDark) {
            titleTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
        supportActionBar?.apply {
            this.elevation = 0F;
            this.title = ""
            this.setDisplayHomeAsUpEnabled(backNavigation)
        }
    }

    override fun onBackPressed() {
        mProgressTaskCount = 0
        if (refreshPreviousScreen) {
            intent.apply {
                putExtra(E_REFRESH_PREVIOUS_SCREEN, refreshPreviousScreen)
            }.run {
                setResult(RESULT_OK, this)
            }
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun customDialog(layout: Int): Dialog {
        val dialog = Dialog(this)
        dialog.setCanceledOnTouchOutside(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(layout)
            val lp = WindowManager.LayoutParams()
            val window = dialog.window
            lp.copyFrom(window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = lp
        }
        return dialog
    }

    override fun showToast(message: String) {
        this toast message
    }

    override fun showStaticError(message: String) {
    }

    override fun showProgress() {
        if (mProgressTaskCount > 0) return
        mProgressTaskCount += 1
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val layout = findViewById<ContentFrameLayout>(android.R.id.content)
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleLarge)
        val params = FrameLayout.LayoutParams(150, 150)
        params.gravity = Gravity.CENTER
        progressBar!!.setBackgroundResource(R.drawable.bg_progress_dialog)
        layout.addView(progressBar, params)
        progressBar!!.visibility = View.VISIBLE
    }

    override fun dismissProgress() {
        mProgressTaskCount -= 1
        if (mProgressTaskCount > 0) return
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar?.visibility = View.GONE
    }

    override fun sessionExpired() {
        val token = SessionManager.instance().getFcmToken()
        SessionManager.instance().clear()
        SessionManager.instance().setFcmToken(token)
        LaunchActivity.start(this)
        finish()
    }

    override fun dateTimeDialog(
        targetView: EditText, isDateEnabled: Boolean, isTimeEnabled: Boolean
    ) {
        val tempCal = Calendar.getInstance()
        val dialog = customDialog(R.layout.dialog_date_time_picker)
        val datePicker = dialog.findViewById<DatePicker>(R.id.date_picker)
        val timePicker = dialog.findViewById<TimePicker>(R.id.time_picker)
        if (isDateEnabled) {
            datePicker.visibility = View.VISIBLE
        } else {
            datePicker.visibility = View.GONE
        }
        if (isTimeEnabled) {
            (dialog.findViewById<View>(R.id.tv_dialog_title) as TextView).text =
                "Select Date and Time"
            timePicker.visibility = View.VISIBLE
        } else {
            (dialog.findViewById<View>(R.id.tv_dialog_title) as TextView).text = "Select Date"
            timePicker.visibility = View.GONE
        }
        dialog.findViewById<View>(R.id.ll_dialog_close)
            .setOnClickListener { v: View? -> dialog.dismiss() }
        dialog.findViewById<View>(R.id.tv_continue).setOnClickListener { view: View? ->
            tempCal.set(Calendar.YEAR, datePicker.year)
            tempCal.set(Calendar.MONTH, datePicker.month)
            tempCal.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
            if (isTimeEnabled) {
                tempCal.set(Calendar.HOUR_OF_DAY, timePicker.currentHour)
                tempCal.set(Calendar.MINUTE, timePicker.currentMinute)
            } else {
                tempCal.set(Calendar.HOUR_OF_DAY, 0)
                tempCal.set(Calendar.MINUTE, 0)
            }
            tempCal.set(Calendar.SECOND, 0)
            tempCal.set(Calendar.MILLISECOND, 0)
            targetView.setText(
                if (isDateEnabled && isTimeEnabled) TimeUtil.millisToDateFormat(
                    tempCal.timeInMillis, TimeUtil.EXPENSE_DISPLAY_DATE_FORMAT
                )
                else TimeUtil.millisToDateFormat(
                    tempCal.timeInMillis, TimeUtil.EXPENSE_DISPLAY_ONLY_DATE_FORMAT
                )
            )
            dialog.dismiss()
        }
        dialog.show()
    }

    protected fun refreshPreviousScreen() {
        refreshPreviousScreen = true
    }
}