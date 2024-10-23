package com.paycraft.user.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.View.GONE
import android.view.View.OnLongClickListener
import android.view.View.VISIBLE
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.paycraft.BuildConfig
import com.paycraft.home.LaunchActivity
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.SessionManager
import com.paycraft.base.isValidEmail
import com.paycraft.databinding.ActivityLoginBinding
import com.paycraft.home.HomeActivity
import com.paycraft.user.forgot.ForgotPasswordActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity(), LoginView, OnLongClickListener {

    private lateinit var mViewModel: LogInViewModel
    private lateinit var mBinding: ActivityLoginBinding

    companion object {
        fun start(context: Context) {
            Intent(context, LoginActivity::class.java)
                .apply {
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.run {
                    context.startActivity(this)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, getString(R.string.title_login), isDark = true
        )
        mBinding.loginTv.setOnLongClickListener(this)
        mViewModel = ViewModelProvider(this)[LogInViewModel::class.java]
        mViewModel.setView(this)
        mBinding.emailEt.addTextChangedListener { mBinding.errorTv.visibility = GONE }
        mBinding.passwordEt.addTextChangedListener { mBinding.errorTv.visibility = GONE }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        LaunchActivity.start(this)
        finish()
    }

    fun onClick(view: View) {
        when (view.id) {
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

            R.id.forgotYourPasswordTv -> {
                ForgotPasswordActivity.start(this)
            }

            R.id.loginTv -> {
                val email = mBinding.emailEt.text.toString()
                val password = mBinding.passwordEt.text.toString()
                if (!email.isValidEmail()) {
                    showStaticError(getString(R.string.email_validation))
                    return
                }
                if (password.isEmpty()) {
                    showStaticError("Password is empty!")
                    return
                }
                if (SessionManager.instance().getFcmToken().isNotEmpty()) {
                    mViewModel.signIn(email, password)
                    return
                }
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task: Task<String?> ->
                        if (!task.isSuccessful) {
                            return@addOnCompleteListener
                        }
                        // Get new FCM registration token
                        task.result?.let { t ->
                            SessionManager.instance().setFcmToken(t)
                        }
                        mViewModel.signIn(email, password)
                    }
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        if (!BuildConfig.DEBUG) return true
        when (BuildConfig.FLAVOR) {
            "prod" -> {
//                mBinding.emailEt.setText("spendprod2@yopmail.com")
//                mBinding.passwordEt.setText("spend@123")

//                mBinding.emailEt.setText("aanchal.pandey@paycraftsol.com")
//                mBinding.passwordEt.setText("Dec@2022")

                mBinding.emailEt.setText("agent@yopmail.com")
                mBinding.passwordEt.setText("Spend@123")

//                mBinding.emailEt.setText("saisandeep@purpleplum.design")
//                mBinding.passwordEt.setText("spend@1234")

//                mBinding.emailEt.setText("ravijain@paycraftsol.com")
//                mBinding.passwordEt.setText("Ravi20@123")

//                mBinding.emailEt.setText("nagarjuna.bogala@purpleplum.design")
//                mBinding.passwordEt.setText("Spend@123")

//                mBinding.emailEt.setText("appflow@yopmail.com")
//                mBinding.passwordEt.setText("Spend@123")
            }

            "dev" -> {
//                mBinding.emailEt.setText("shilpashri@purpleplum.design")
//                mBinding.passwordEt.setText("spend@1234")

//                mBinding.emailEt.setText("appflow@yopmail.com")
//                mBinding.passwordEt.setText("Spend@123")

//                mBinding.emailEt.setText("vamsi.datti@purpleplumfi.com")
//                mBinding.passwordEt.setText("Spend@123")

//                mBinding.emailEt.setText("fidel.kunhi@paycraftsol.com")
//                mBinding.passwordEt.setText("Paycraft@123")

//                mBinding.emailEt.setText("manasa123@yopmail.com")
//                mBinding.passwordEt.setText("Spend@123")

//                mBinding.emailEt.setText("testuser@yopmail.com")
//                mBinding.passwordEt.setText("Spend@123")

//                mBinding.emailEt.setText("fidel.kunhi@paycraftsol.com")
//                mBinding.passwordEt.setText("Paycraft@123")

//                mBinding.emailEt.setText("jack123@yopmail.com")
//                mBinding.passwordEt.setText("Spend@123")

//                mBinding.emailEt.setText("anno1234@yopmail.com")
//                mBinding.passwordEt.setText("Spend@123")

//                mBinding.emailEt.setText("fidel.kunhi@paycraftsol.com")
//                mBinding.passwordEt.setText("Paycraft@123")

//                mBinding.emailEt.setText("sanjayadapa7@gmail.com")
//                mBinding.passwordEt.setText("Spend@123")

//                mBinding.emailEt.setText("kranti.sherkar@paycraftsol.com")
//                mBinding.passwordEt.setText("Test@1234")

//                mBinding.emailEt.setText("agent@yopmail.com")
//                mBinding.passwordEt.setText("Spend@123")

                mBinding.emailEt.setText("dhantech@yopmail.com")
                mBinding.passwordEt.setText("Spend@123")

//                mBinding.emailEt.setText("anno1234@yopmail.com")
//                mBinding.passwordEt.setText("spend@123")
            }

            "loc" -> {
                mBinding.emailEt.setText("spendpro2@yopmail.com")
                mBinding.passwordEt.setText("spend@123")
            }
        }
        return true
    }

    override fun onLoginSuccess() {
        HomeActivity.start(this)
        finish()
    }

    override fun showStaticError(text: String) {
        mBinding.errorTv.visibility = VISIBLE
        mBinding.errorTv.text = text
    }
}