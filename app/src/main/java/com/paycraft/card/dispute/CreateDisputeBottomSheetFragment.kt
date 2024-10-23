package com.paycraft.card.dispute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.paycraft.R
import com.paycraft.ems.SuccessActivity
import com.paycraft.base.BaseBottomSheetDialogFragment
import com.paycraft.databinding.FragmentBottomSheetCreateDisputeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateDisputeBottomSheetFragment : BaseBottomSheetDialogFragment(), View.OnClickListener {
    companion object {
        val TAG = "CreateDispute"
        fun start(
            fm: FragmentManager
        ): CreateDisputeBottomSheetFragment {
            val fragment = CreateDisputeBottomSheetFragment()
            fragment.show(fm, TAG)
            return fragment
        }
    }

    lateinit var mBinding: FragmentBottomSheetCreateDisputeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_create_dispute,
            container,
            false
        )
        mBinding.submitCommentTv.setOnClickListener(this)
        return mBinding.root
    }

    override fun onClick(v: View) {
        SuccessActivity.start(
            requireContext(),
            "Dispute Raised",
            "Your dispute record raised successfully. Customer support will contact you in 48 hours."
        )
        dismiss()
    }
}