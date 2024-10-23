package com.paycraft.ems.attachments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.paycraft.R
import com.paycraft.base.BaseFragment
import com.paycraft.databinding.FragmentReportAttachmentBinding
import com.paycraft.ems.transactions.AttachmentsAdapter
import com.paycraft.ems.transactions.detail.RecordFragmentListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttachmentsFragment : BaseFragment() {

    companion object {
        const val TAG = "Attachments"
        fun newInstance(): AttachmentsFragment {
            return AttachmentsFragment()
        }
    }

    lateinit var mBinding: FragmentReportAttachmentBinding
    private lateinit var mTransactionAttachmentsPagerAdapter: AttachmentsAdapter
    private var recordFragmentListener: RecordFragmentListener? = null
    val mViewModel by viewModels<AttachmentsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_report_attachment, container, false)
        recordFragmentListener = activity as? RecordFragmentListener
        mViewModel.files.observe(viewLifecycleOwner) {
            mBinding.attachmentsSrl.isRefreshing = false
            mBinding.loadingSkv.visibility = GONE
            mTransactionAttachmentsPagerAdapter = AttachmentsAdapter(
                requireContext(), it
            )
            mBinding.imagesVp.adapter = mTransactionAttachmentsPagerAdapter
            mBinding.indiacatorTabLayout.setupWithViewPager(mBinding.imagesVp, true)
        }
        mBinding.attachmentsSrl.setOnRefreshListener {
            load()
        }
        load()
        return mBinding.root
    }

    private fun load() {
        recordFragmentListener?.getRecord()?.id?.let {
            mBinding.loadingSkv.visibility = VISIBLE
            mViewModel.getTransactionAttachments(it)
        }
    }

}