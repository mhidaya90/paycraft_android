package com.paycraft.user.forgot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.isValidEmail
import com.paycraft.databinding.ActivityForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity(), ForgotPasswordView, View.OnClickListener {
    lateinit var mBinding: ActivityForgotPasswordBinding

    companion object {
        fun start(context: Context) {
            Intent(context, ForgotPasswordActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.run {
                    context.startActivity(this)
                }
        }
    }

    lateinit var mViewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv,
            getString(R.string.title_forget_password),
            isDark = true
        )
        mBinding.resetPasswordTv.setOnClickListener(this)
        mViewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        mViewModel.setView(this)
        mBinding.emailEt.addTextChangedListener { mBinding.errorTv.visibility = View.GONE }
    }

    override fun onForgotPasswordSuccess() {
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.resetPasswordTv -> {
                val email = mBinding.emailEt.text.toString()
                if (!email.isValidEmail()) {
                    showStaticError(getString(R.string.email_validation))
                    return
                }
                mViewModel.forgotPassword(email)
            }
        }
    }

    override fun showStaticError(text: String) {
        mBinding.errorTv.visibility = View.VISIBLE
        mBinding.errorTv.text = text
    }
}