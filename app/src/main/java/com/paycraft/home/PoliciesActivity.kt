package com.paycraft.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.card.add.kyc.AddCardKycActivity
import com.paycraft.databinding.ActivityPoliciesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PoliciesActivity : BaseActivity(), OnClickListener {
    lateinit var binding: ActivityPoliciesBinding

    companion object {
        fun start(activity: Activity) {
            Intent(activity, PoliciesActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.run {
                    activity.startActivity(this)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_policies)
        configToolbar(
            binding.toolbarLayout.toolbar,
            binding.toolbarLayout.toolbarTitleTv,
            getString(R.string.privacy_policy)
        )
        binding.grievanceTv.setOnClickListener(this)
        binding.privacyTv.setOnClickListener(this)
        binding.refundTv.setOnClickListener(this)
        binding.tAndCsTv.setOnClickListener(this)
        binding.faqTv.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.grievanceTv -> {
                AddCardKycActivity.start(
                    this,
                    findViewById<TextView>(R.id.grievanceTv).text.toString(),
                    url = "https://emsweb.paycraftsol.com/grievance-policy",
                    showDone = false,
                    navigateToHomeOnDone = false,
                    navigateBackWithOutConsent = true
                )
            }

            R.id.privacyTv -> {
                AddCardKycActivity.start(
                    this,
                    findViewById<TextView>(R.id.privacyTv).text.toString(),
                    "https://emsweb.paycraftsol.com/privacy",
                    showDone = false,
                    navigateToHomeOnDone = false,
                    navigateBackWithOutConsent = true
                )
            }

            R.id.refundTv -> {
                AddCardKycActivity.start(
                    this,
                    findViewById<TextView>(R.id.refundTv).text.toString(),
                    "https://emsweb.paycraftsol.com/refund-policy",
                    showDone = false,
                    navigateToHomeOnDone = false,
                    navigateBackWithOutConsent = true
                )
            }

            R.id.tAndCsTv -> {
                AddCardKycActivity.start(
                    this,
                    findViewById<TextView>(R.id.tAndCsTv).text.toString(),
                    "https://emsweb.paycraftsol.com/terms-n-conditions",
                    showDone = false,
                    navigateToHomeOnDone = false,
                    navigateBackWithOutConsent = true
                )
            }

            R.id.faqTv -> {
                AddCardKycActivity.start(
                    this,
                    findViewById<TextView>(R.id.faqTv).text.toString(),
                    "https://emsweb.paycraftsol.com/faqs",
                    showDone = false,
                    navigateToHomeOnDone = false,
                    navigateBackWithOutConsent = true
                )
            }
        }
    }
}