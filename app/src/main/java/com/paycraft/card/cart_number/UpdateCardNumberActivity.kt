package com.paycraft.card.cart_number

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.dialog
import com.paycraft.base.toast
import com.paycraft.card.add.verify.CardVerificationActivity
import com.paycraft.databinding.ActivityUpdateCardNumberBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateCardNumberActivity : BaseActivity(), View.OnClickListener, UpdateCardNumberView {
    companion object {
        fun start(context: Context, id: String) {
            Intent(context, UpdateCardNumberActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(CardVerificationActivity.E_CARD_REF_ID, id)
            }.run {
                context.startActivity(this)
            }
        }
    }

    lateinit var binding: ActivityUpdateCardNumberBinding
    lateinit var viewModel: UpdateCardNumberViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateCardNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.confirmTv.setOnClickListener(this)
        configToolbar(
            binding.toolbarLayout.toolbar,
            binding.toolbarLayout.toolbarTitleTv, getString(R.string.title_card_verification)
        )
        viewModel = ViewModelProvider(this)[UpdateCardNumberViewModel::class.java]
        viewModel.setView(this)
        viewModel.id = intent.getStringExtra(CardVerificationActivity.E_CARD_REF_ID)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.confirmTv -> {
                val mobile = binding.mobileNumberEt.text.toString()
                if (mobile.isEmpty()) {
                    toast("Please enter mobile number!")
                    return
                }
                dialog("Are you sure! do you want to update mobile number?", "Yes", "No") { _, _ ->
                    viewModel.updateCardNumber(mobile)
                }
            }
        }
    }

    override fun onUpdateCardNumberSuccess() {
        finish()
    }
}