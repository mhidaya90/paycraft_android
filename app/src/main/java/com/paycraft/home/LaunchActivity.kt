package com.paycraft.home

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.Bundle
import android.view.View
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.SessionManager
import com.paycraft.base.isEmulator
import com.paycraft.base.toast
import com.paycraft.user.login.LoginActivity
import com.paycraft.user.signup.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@AndroidEntryPoint
class LaunchActivity : BaseActivity() {

    companion object {
        val TAG = "LaunchActivity"
        fun intent(context: Context): Intent {
            val intent = Intent(context, LaunchActivity::class.java)
            intent.flags =
                FLAG_ACTIVITY_CLEAR_TOP and FLAG_ACTIVITY_CLEAR_TASK and FLAG_ACTIVITY_NEW_TASK
            return intent
        }

        fun start(context: Context) {
            context.startActivity(intent(context))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        if (checkRootMethod1() || checkRootMethod2() || checkRootMethod3()) {
            this toast "Can not used app in rooted devices!"
            return
        }
        if (isEmulator() && SessionManager.instance().getHeader().isNotEmpty()) {
            launchHome()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && SessionManager.instance().getHeader().isNotEmpty()
        ) {
            val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            if (km.isKeyguardSecure) {
                val authIntent = km.createConfirmDeviceCredentialIntent(
                    "Unlock ${getString(R.string.app_name)}",
                    "Simpy touch you finger to login"
                )
                startActivityForResult(authIntent, 100)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            HomeActivity.start(this)
            finish()
        } else {
            start(this)
            finish()
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.loginTv -> {
                LoginActivity.start(this)
                finish()
            }

            R.id.signUpTv -> {
                SignUpActivity.start(this)
                finish()
            }
        }
    }

    private fun launchHome() {
        HomeActivity.start(this@LaunchActivity)
        finish()
    }

    private fun checkRootMethod1(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootMethod2(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            `in`.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }
}