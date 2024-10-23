package com.paycraft.card.add.kyc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.paycraft.R
import com.paycraft.ems.SuccessActivity
import com.paycraft.base.BaseActivity
import com.paycraft.base.dialog
import com.paycraft.databinding.ActivityAddCardKycBinding
import com.paycraft.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCardKycActivity : BaseActivity() {
    companion object {
        val E_URL = "url"
        val E_TITLE = "title"
        val E_SHOW_DONE = "showDone"
        val E_NAVIGATE_TO_HOME_ON_DONE = "navigateToHomeOnDone"
        val E_NAVIGATE_BACK_WITH_OUT_CONFIRMARION = "navigateBackWithOutConsent"
        fun start(
            context: Context, title: String = "",
            url: String,
            showDone: Boolean = true,
            navigateToHomeOnDone: Boolean = true,
            navigateBackWithOutConsent: Boolean = false
        ) {
            Intent(context, AddCardKycActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(E_URL, url)
                    putExtra(E_TITLE, title)
                    putExtra(E_SHOW_DONE, showDone)
                    putExtra(E_NAVIGATE_TO_HOME_ON_DONE, navigateToHomeOnDone)
                    putExtra(E_NAVIGATE_BACK_WITH_OUT_CONFIRMARION, navigateBackWithOutConsent)
                }.run {
                    context.startActivity(this)
                }
        }
    }

    lateinit var mBinding: ActivityAddCardKycBinding
    var navigateBackWithOutConsent = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_card_kyc)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv,
            intent?.getStringExtra(E_TITLE) ?: ""
        )
        navigateBackWithOutConsent =
            intent?.getBooleanExtra(E_NAVIGATE_BACK_WITH_OUT_CONFIRMARION, true) ?: true

        mBinding.kyvWv.webChromeClient = WebChromeClient()
        mBinding.kyvWv.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.d("TAG", "onReceivedError: $request $error")
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                Log.d("TAG", "onLoadResource: $url ")
                if ("http://ems-webuat.paycraftsol.com/pinset" == url) {
                    SuccessActivity.start(
                        this@AddCardKycActivity,
                        getString(R.string.msg_short_pin_change),
                        getString(R.string.msg_pin_chaged)
                    )
                    finish()
                } else if (url?.contains("api/cards/payment_gateway_redirect") == true) {
                    navigateBackWithOutConsent = true
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        finish()
                    }, 5000)
                } else {
                    super.onLoadResource(view, url)
                }
            }
        }
        mBinding.kyvWv.settings.javaScriptEnabled = true
        mBinding.kyvWv.settings.javaScriptCanOpenWindowsAutomatically = true

        intent.getStringExtra(E_URL)?.let { mBinding.kyvWv.loadUrl(it) }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (navigateBackWithOutConsent) {
            super.onBackPressed()
        } else {
            dialog("Are you sure you want cancel the current transaction?", "Yes", "No") { _, _ ->
                super.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (intent.getBooleanExtra(E_SHOW_DONE, false))
            menuInflater.inflate(R.menu.menu_kyc, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.doneOpn -> {
                if (intent.getBooleanExtra(E_NAVIGATE_TO_HOME_ON_DONE, false))
                    HomeActivity.start(this)
                else
                    finish()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }
}