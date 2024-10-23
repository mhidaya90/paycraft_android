package com.paycraft.ems.transactions.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.base.BaseFragment
import com.paycraft.databinding.FragmentDetailsBinding
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.detail.TransactionActivity.Companion.E_TRANSACTION_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : BaseFragment() {

    companion object {
        const val TAG = "Details"

        fun newInstance(id: String?): DetailsFragment {
            val args = Bundle()
            args.putString(E_TRANSACTION_ID, id)
            val fragment = DetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var mBinding: FragmentDetailsBinding
    private lateinit var mDisplayFieldAdapter: DisplayFieldAdapter
    var mFields: List<FieldOption>? = null
    private var recordFragmentListener: RecordFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recordFragmentListener = activity as? RecordFragmentListener
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        mFields = recordFragmentListener?.getRecord()?.mFields ?: emptyList()
        activity?.let {
            mDisplayFieldAdapter = DisplayFieldAdapter(
                it,
                mFields ?: emptyList()
            )
            mBinding.customFieldsRv.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            mBinding.customFieldsRv.adapter = mDisplayFieldAdapter
        }
        return mBinding.root
    }
}