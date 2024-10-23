package com.paycraft.ems

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.databinding.ActivitySuccessBinding
import com.paycraft.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessActivity : BaseActivity(), View.OnClickListener {
    companion object {
        val E_SHORT_MESSAGE = "msg"
        val E_MESSAGE = "message"
        val E_INFO = "info"
        fun start(
            context: Context,
            msg: String,
            message: String = "",
            info: String = ""
        ) {
            Intent(context, SuccessActivity::class.java)
                .apply {
                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(E_MESSAGE, message)
                    putExtra(E_SHORT_MESSAGE, msg)
                    putExtra(E_INFO, info)
                }.run {
                    context.startActivity(this)
                }
        }
    }

    private lateinit var mBinding: ActivitySuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_success)
        mBinding.doneTv.setOnClickListener(this)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, "Cards"
        )

        mBinding.successSmallMsgTv.text = intent.getStringExtra(E_SHORT_MESSAGE)

        val message = intent.getStringExtra(E_MESSAGE)
        mBinding.successMsgTv.visibility = if (message.isNullOrEmpty()) GONE else VISIBLE
        mBinding.successMsgTv.text = message

        var infoMsg = intent.getStringExtra(E_INFO)
        infoMsg = infoMsg?.replace("\n", "")
        mBinding.infoMsgTv.text = infoMsg
        mBinding.infoMsgTv.visibility = if (infoMsg.isNullOrEmpty()) GONE else VISIBLE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.doneTv -> {
                HomeActivity.start(this)
                finish()
            }
        }
    }
}