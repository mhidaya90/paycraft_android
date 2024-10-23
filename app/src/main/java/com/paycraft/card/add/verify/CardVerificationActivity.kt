package com.paycraft.card.add.verify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.paycraft.R
import com.paycraft.ems.SuccessActivity
import com.paycraft.base.BaseActivity
import com.paycraft.base.Validator
import com.paycraft.base.toast
import com.paycraft.card.add.ActivateCardInfoActivity
import com.paycraft.databinding.ActivityCardVerificationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardVerificationActivity : BaseActivity(), AddCardVerifyOtpView {
    lateinit var mBinding: ActivityCardVerificationBinding

    companion object {
        val E_CARD_REF_ID = "card_ref_id"
        val E_TRX_ID = "trx_id"
        val E_IS_PRE_ADDED = "pre_added"
        fun start(context: Context, isPreAdded: Boolean, id: String, trxId: String) {
            Intent(context, CardVerificationActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(E_CARD_REF_ID, id)
                    putExtra(E_TRX_ID, trxId)
                    putExtra(E_IS_PRE_ADDED, isPreAdded)
                }.run {
                    context.startActivity(this)
                }
        }
    }

    lateinit var mCardRefId: String
    lateinit var mTrxId: String
    lateinit var mViewModel: AddCardVerifyOtpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_card_verification)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, getString(R.string.title_card_verification)
        )
        mCardRefId = intent.getStringExtra(E_CARD_REF_ID)!!
        mTrxId = intent.getStringExtra(E_TRX_ID)!!
        mViewModel = ViewModelProvider(this)[AddCardVerifyOtpViewModel::class.java]
        mViewModel.setView(this)
        mBinding.otpEt.addTextChangedListener {
            apiAfter()
        }
    }

    fun onClick(view: android.view.View) {
        when (view.id) {
            R.id.confirmTv -> {
                val otp = mBinding.otpEt.text.toString()
                if (Validator.isNullOrEmpty(otp)) {
                    toast("Please enter OTP!")
                    return
                }
                apiBefore()
                mViewModel.addCardVerifyOtp(mCardRefId, otp, mTrxId)
            }
        }
    }

    override fun onActivateCard(res: AddCardActivateRes) {
        when (res.code) {
            RESPONSE_00 -> {
                res.url?.let {
                    ActivateCardInfoActivity.start(this, it)
                    finish()
                }
            }

            RESPONSE_CMS149 -> {
                SuccessActivity.start(
                    this,
                    "Card Activation in Progress.",
                    "Your card will be activated post successful verification of the KYC by the bank. It might take upto 48 hrs to activate the card.",
                    "Haven’t completed the activation? You can finish it using the link shared on the your Registered mobile number or retry after 48hrs."
                )
                finish()
            }

            RESPONSE_200 -> {
                SuccessActivity.start(
                    this,
                    "Card Activated Successfully."
                )
                finish()
            }

            else -> {
                toast(res.message ?: "Something went wrong!")
                finish()
            }
        }
    }

    override fun apiBefore() {
        mBinding.confirmTv.isEnabled = false
    }

    override fun apiAfter() {
        mBinding.confirmTv.isEnabled = true
    }

}