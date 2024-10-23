package com.paycraft.user.signup

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.paycraft.home.LaunchActivity
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.isValidEmail
import com.paycraft.base.isValidPhone
import com.paycraft.databinding.ActivitySignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BaseActivity(), SignUpView, View.OnClickListener {
    lateinit var mSignUpViewModel: SignUpViewModel
    lateinit var mBinding: ActivitySignUpBinding

    companion object {
        fun start(context: Context) {
            Intent(context, SignUpActivity::class.java)
                .apply {
                    this.flags = FLAG_ACTIVITY_CLEAR_TOP
                }.run {
                    context.startActivity(this)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv,
            getString(R.string.title_sign_up), isDark = true
        )
        mBinding.signupDoneTv.setOnClickListener(this)
        mBinding.signUpTv.setOnClickListener(this)
        mBinding.passwordEye.setOnClickListener(this)
        mSignUpViewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        mSignUpViewModel.setView(this)

        mBinding.nameEt.addTextChangedListener { mBinding.errorTv.visibility = View.GONE }
        mBinding.emailEt.addTextChangedListener { mBinding.errorTv.visibility = View.GONE }
        mBinding.mobileEt.addTextChangedListener { mBinding.errorTv.visibility = View.GONE }
        mBinding.passwordEt.addTextChangedListener { mBinding.errorTv.visibility = View.GONE }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        LaunchActivity.start(this)
        finish()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.passwordEye -> {
                mSignUpViewModel.showPassword = !mSignUpViewModel.showPassword
                if (mSignUpViewModel.showPassword) {
                    mBinding.passwordEye.setImageDrawable(getDrawable(R.drawable.show_eye))
                    mBinding.passwordEt.transformationMethod =
                        HideReturnsTransformationMethod.getInstance();
                } else {
                    mBinding.passwordEye.setImageDrawable(getDrawable(R.drawable.hide_eye))
                    mBinding.passwordEt.transformationMethod =
                        PasswordTransformationMethod.getInstance();
                }
            }

            R.id.signupDoneTv -> {
                onBackPressed()
            }

            R.id.signUpTv -> {
                val name = mBinding.nameEt.text.toString()
                val email = mBinding.emailEt.text.toString()
                val mobile = mBinding.mobileEt.text.toString()
                val password = mBinding.passwordEt.text.toString()
                if (name.isEmpty()) {
                    showStaticError("Name is empty!")
                    return
                }
                if (!email.isValidEmail()) {
                    showStaticError(getString(R.string.email_validation))
                    return
                }
                if (!mobile.isValidPhone()) {
                    showStaticError("Mobile is empty!")
                    return
                }
                if (password.isEmpty()) {
                    showStaticError("Password is empty!")
                    return
                }
                mSignUpViewModel.signUp(name, email, mobile, password)
            }
        }
    }

    override fun onSignUpSuccess() {
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv,
            getString(R.string.verify_account), isDark = true
        )
        mBinding.verificationInfoLl.visibility = View.VISIBLE
        mBinding.emailInfoTextTv.text =
            getString(R.string.link_to_your_account, mBinding.emailEt.text.toString())
    }

    override fun showStaticError(text: String) {
        mBinding.errorTv.visibility = View.VISIBLE
        mBinding.errorTv.text = text
    }
}