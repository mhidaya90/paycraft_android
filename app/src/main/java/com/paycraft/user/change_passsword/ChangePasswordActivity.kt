package com.paycraft.user.change_passsword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.hideKeyboard
import com.paycraft.base.isValidatePassword
import com.paycraft.base.toast
import com.paycraft.databinding.ActivityChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity(), ChangePasswordView, View.OnClickListener {
    companion object {
        fun start(context: Context) {
            Intent(context, ChangePasswordActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.run {
                    context.startActivity(this)
                }
        }
    }

    private lateinit var mBinding: ActivityChangePasswordBinding
    private lateinit var mViewModel: ChangePasswordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, getString(R.string.change_password)
        )
        mBinding.submitTv.setOnClickListener(this)
        mBinding.currentPasswordEye.setOnClickListener(this)
        mBinding.passwordEye.setOnClickListener(this)
        mBinding.confirmPasswordEye.setOnClickListener(this)
        mViewModel = ViewModelProvider(this)[ChangePasswordViewModel::class.java]
        mViewModel.setView(this)
    }

    override fun onChangePasswordSuccess() {
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.currentPasswordEye -> {
                mViewModel.currentShowPassword = !mViewModel.currentShowPassword
                if (mViewModel.currentShowPassword) {
                    mBinding.currentPasswordEye.setImageDrawable(getDrawable(R.drawable.show_eye))
                    mBinding.currentPasswordEv.transformationMethod =
                        HideReturnsTransformationMethod.getInstance();
                } else {
                    mBinding.currentPasswordEye.setImageDrawable(getDrawable(R.drawable.hide_eye))
                    mBinding.currentPasswordEv.transformationMethod =
                        PasswordTransformationMethod.getInstance();
                }
            }

            R.id.passwordEye -> {
                mViewModel.showPassword = !mViewModel.showPassword
                if (mViewModel.showPassword) {
                    mBinding.passwordEye.setImageDrawable(getDrawable(R.drawable.show_eye))
                    mBinding.passwordEt.transformationMethod =
                        HideReturnsTransformationMethod.getInstance();
                } else {
                    mBinding.passwordEye.setImageDrawable(getDrawable(R.drawable.hide_eye))
                    mBinding.passwordEt.transformationMethod =
                        PasswordTransformationMethod.getInstance();
                }
            }

            R.id.confirmPasswordEye -> {
                mViewModel.confirmShowPassword = !mViewModel.confirmShowPassword
                if (mViewModel.confirmShowPassword) {
                    mBinding.confirmPasswordEye.setImageDrawable(getDrawable(R.drawable.show_eye))
                    mBinding.confirmPasswordEt.transformationMethod =
                        HideReturnsTransformationMethod.getInstance();
                } else {
                    mBinding.confirmPasswordEye.setImageDrawable(getDrawable(R.drawable.hide_eye))
                    mBinding.confirmPasswordEt.transformationMethod =
                        PasswordTransformationMethod.getInstance();
                }
            }

            R.id.submitTv -> {
                hideKeyboard()
                val currentPassword = mBinding.currentPasswordEv.text.toString()
                val password = mBinding.passwordEt.text.toString()
                val confirmPassword = mBinding.confirmPasswordEt.text.toString()
                if (currentPassword.isEmpty()) {
                    toast("Current password is empty!")
                    return
                }
                val passwordError = password.isValidatePassword()
                if (passwordError.isNotEmpty()) {
                    toast(passwordError)
                    return
                }
                val confirmPasswordError = confirmPassword.isValidatePassword()
                if (confirmPasswordError.isNotEmpty()) {
                    toast(confirmPasswordError)
                    return
                }
                if (confirmPassword != password) {
                    toast("Passwords should match!")
                    return
                }
                if (currentPassword == password) {
                    toast("Current and new password should not be same!")
                    return
                }
                mViewModel.changePassword(currentPassword, password, confirmPassword)
            }
        }
    }
}