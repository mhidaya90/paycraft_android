package com.paycraft.card.add

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View.OnClickListener
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.toast
import com.paycraft.databinding.ActivityActivateCardInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivateCardInfoActivity : BaseActivity(), OnClickListener {
    lateinit var mBinding: ActivityActivateCardInfoBinding

    companion object {
        val E_URL = "url"
        fun start(context: Context, url: String) {
            Intent(context, ActivateCardInfoActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(E_URL, url)
                }.run {
                    context.startActivity(this)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_activate_card_info)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, getString(R.string.title_card_activation)
        )
        mBinding.openUrlTv.setOnClickListener(this)
    }

    override fun onClick(view: android.view.View) {
        when (view.id) {
            R.id.openUrlTv -> {
                intent.getStringExtra(E_URL)?.let {
                    handleUrl(it)
                }
            }
        }
    }

    private fun handleUrl(url: String?) {
        if (url.isNullOrEmpty()) {
            toast("Something went wrong!")
            return
        }
        CustomTabsIntent.Builder().apply {
            setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(Color.parseColor("#FFFFFFFF"))
                    .build()
            )
            setCloseButtonIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.baseline_arrow_back_black_24
                )
            )
            setDefaultShareMenuItemEnabled(false)
            setShowTitle(true)
        }.build().apply {
            intent.setPackage("com.android.chrome")
        }.run {
            launchUrl(this@ActivateCardInfoActivity, Uri.parse(url))
        }
        finish()
    }
}