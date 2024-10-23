package com.paycraft.card.add.otp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.Validator
import com.paycraft.base.toast
import com.paycraft.card.add.verify.CardVerificationActivity
import com.paycraft.databinding.ActivityAddCardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCardActivity : BaseActivity(), AddCarView, View.OnClickListener {
    lateinit var mBinding: ActivityAddCardBinding

    companion object {
        fun start(context: Context) {
            Intent(context, AddCardActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.run {
                    context.startActivity(this)
                }
        }
    }

    lateinit var mViewModel: AddCardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_card)
        mBinding.proceedTv.setOnClickListener(this)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, getString(R.string.title_add_new_card)
        )
        mViewModel = ViewModelProvider(this)[AddCardViewModel::class.java]
        mViewModel.setView(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.proceedTv -> {
                val id = mBinding.cardRefIdEt.text.toString()
                if (Validator.isNullOrEmpty(id)) {
                    toast("Please enter card reference id.")
                    return
                }
                mViewModel.cardRegister(id)
            }
        }
    }

    override fun onOtpSent(id: String) {
        CardVerificationActivity.start(this, false, mBinding.cardRefIdEt.text.toString(), id)
        finish()
    }
}