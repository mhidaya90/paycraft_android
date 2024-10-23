package com.paycraft.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.databinding.DataBindingUtil
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.databinding.ActivityHelpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HelpActivity : BaseActivity(), OnClickListener {
    private lateinit var mBinding: ActivityHelpBinding

    companion object {
        fun start(context: Context) {
            Intent(context, HelpActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.run {
                    context.startActivity(this)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_help)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, getString(R.string.help_and_support)
        )
        mBinding.firstCallTv.setOnClickListener(this)
        mBinding.secondCallTv.setOnClickListener(this)
        mBinding.emailTv.setOnClickListener(this)
        mBinding.gEmailTv.setOnClickListener(this)
        mBinding.gSecondCallTv.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.firstCallTv -> {
                startActivity(Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:7400088941")
                })
            }

            R.id.gSecondCallTv, R.id.secondCallTv -> {
                startActivity(Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:022-42022190")
                })
            }

            R.id.emailTv -> {
//                val mailto = "mailto:bob@example.org" +
//                        "?cc=" + "alice@example.com" +
//                        "&subject=" + Uri.encode(subject) +
//                        "&body=" + Uri.encode(bodyText)
                val mailto = "mailto:" + getString(R.string.support_email)
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse(mailto)
                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    //TODO: Handle case where no email app is available
                }
            }

            R.id.gEmailTv -> {
                val mailto = "mailto:" + getString(R.string.support_grievance_email)
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse(mailto)
                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    //TODO: Handle case where no email app is available
                }
            }
        }
    }
}