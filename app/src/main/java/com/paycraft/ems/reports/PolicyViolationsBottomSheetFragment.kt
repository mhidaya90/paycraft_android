package com.paycraft.ems.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.base.BaseBottomSheetDialogFragment
import com.paycraft.databinding.DialogPolicyViolationsBottomBinding
import com.paycraft.ems.PolicyViolationsBottomSheetFragmentListener
import com.paycraft.ems.reports.detail.DisplayViolation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyViolationsBottomSheetFragment : BaseBottomSheetDialogFragment(), View.OnClickListener {
    companion object {
        private val TAG = "PolicyViolationsBottomSheetFragment"
        private val E_VIOLATIONS = "Violations"

        fun start(
            fragmentManager: FragmentManager?,
            violations: List<DisplayViolation>?
        ): PolicyViolationsBottomSheetFragment {
            val fragment = PolicyViolationsBottomSheetFragment()
            fragmentManager?.let {
                fragment.show(fragmentManager, TAG)
            }
            val b = Bundle()
            violations?.let {
                b.putParcelableArrayList(
                    E_VIOLATIONS,
                    it as ArrayList<DisplayViolation>
                )
            }
            fragment.arguments = b
            return fragment
        }
    }

    lateinit var binding: DialogPolicyViolationsBottomBinding
    var violations: ArrayList<DisplayViolation>? = null
    lateinit var listner: PolicyViolationsBottomSheetFragmentListener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listner = requireActivity() as PolicyViolationsBottomSheetFragmentListener
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_policy_violations_bottom,
            container,
            false
        )
        binding.cancelTv.setOnClickListener(this)
        binding.continueTv.setOnClickListener(this)

        violations = arguments?.getParcelableArrayList(E_VIOLATIONS) ?: arrayListOf()
        binding.policyLimitsRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = PolicyLimitsAdapter(requireContext(), violations!!)
        }
        if (violations!!.any { true == it.isBlocked }) {
            binding.continueTv.visibility = GONE
        }
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancelTv -> {
                dismiss()
            }
            R.id.continueTv -> {
                dismiss()
                listner.continueWithWarnings()
            }
        }
    }
}