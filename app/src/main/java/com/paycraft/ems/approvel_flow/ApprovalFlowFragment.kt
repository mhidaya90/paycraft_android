package com.paycraft.ems.approvel_flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.paycraft.R
import com.paycraft.base.BaseFragment
import com.paycraft.databinding.FragmentApprovalFlowBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApprovalFlowFragment : BaseFragment() {

    companion object {
        const val TAG = "Approval Flow"
        fun newInstance(): ApprovalFlowFragment {
            val args = Bundle()
            val fragment = ApprovalFlowFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var mBinding: FragmentApprovalFlowBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_approval_flow, container, false)
        return mBinding.root
    }
}