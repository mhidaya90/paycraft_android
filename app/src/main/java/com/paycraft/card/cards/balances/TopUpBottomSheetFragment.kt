package com.paycraft.card.cards.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.paycraft.R
import com.paycraft.base.BaseBottomSheetDialogFragment
import com.paycraft.base.disable
import com.paycraft.base.enable
import com.paycraft.base.restrictDecimalTo
import com.paycraft.base.toast
import com.paycraft.card.add.kyc.AddCardKycActivity
import com.paycraft.databinding.FragmentBottomSheetTopUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopUpBottomSheetFragment : BaseBottomSheetDialogFragment(), View.OnClickListener {
    companion object {
        val TAG = "TopUp"
        fun start(
            fm: FragmentManager
        ): TopUpBottomSheetFragment {
            val fragment = TopUpBottomSheetFragment()
            fragment.show(fm, TAG)
            return fragment
        }
    }

    lateinit var mBinding: FragmentBottomSheetTopUpBinding
    var topUpBottomSheetFragmentListener: TopUpBottomSheetFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_top_up,
            container,
            false
        )
        topUpBottomSheetFragmentListener = activity as? TopUpBottomSheetFragmentListener
        mBinding.continueTv.setOnClickListener(this)
        mBinding.cancelTv.setOnClickListener(this)
        mBinding.topUpDismissIv.setOnClickListener(this)
        mBinding.termsAndConditions.setOnClickListener(this)
        mBinding.termsAndConditionsCb.setOnCheckedChangeListener { buttonView, isChecked ->
            handleContinue()
        }
        mBinding.topUpAmountEt.restrictDecimalTo(2)
        return mBinding.root
    }

    private fun handleContinue() {
        if (mBinding.termsAndConditionsCb.isChecked) {
            mBinding.continueTv.enable()
        } else {
            mBinding.continueTv.disable()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancelTv, R.id.topUpDismissIv -> {
                dismiss()
            }

            R.id.termsAndConditions -> {
                AddCardKycActivity.start(
                    requireContext(),
                    getString(R.string.terms_and_conditions),
                    "https://drive.google.com/file/d/1VybSmYD9_EOTszFspsCfrBraK9lCL71h/view?usp=sharing",
                    false,
                    false
                )
            }

            R.id.continueTv -> {
                val amount = mBinding.topUpAmountEt.text.toString().trim()
                if (amount.isEmpty()) {
                    toast("Please enter amount!")
                    return
                }
                var amountValue =
                    mBinding.topUpAmountEt.text.toString().toBigDecimalOrNull()?.toDouble() ?: 0.0
                if (amountValue <= 0.0) {
                    toast("Amount should be grater than 0!")
                    return
                }

                if (!mBinding.termsAndConditionsCb.isChecked) {
                    toast("Read and agree to the terms and conditions to continue!")
                    return
                }
                topUpBottomSheetFragmentListener?.onContinue(
                    TopUpRequest(
                        "",
                        amountValue.toBigDecimal().toPlainString()
                    )
                )
                dismiss()
            }
        }
    }
}
