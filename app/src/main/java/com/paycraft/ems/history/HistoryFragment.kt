package com.paycraft.ems.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.base.BaseFragment
import com.paycraft.base.Paginator
import com.paycraft.databinding.FragmentReportHistoryBinding
import com.paycraft.ems.transactions.detail.RecordFragmentListener
import com.paycraft.ems.transactions.detail.TransactionActivity.Companion.E_TRANSACTION_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseFragment(), HistoryView {

    companion object {
        const val TAG = "History"
        fun newInstance(id: String?): HistoryFragment {
            val args = Bundle()
            args.putString(E_TRANSACTION_ID, id)
            val fragment = HistoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var mBinding: FragmentReportHistoryBinding
    lateinit var mViewModel: HistoryViewModel
    lateinit var mId: String
    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mHistoryAdapter: HistoryAdapter
    private lateinit var mPaginator: Paginator
    private var recordFragmentListener: RecordFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_report_history, container, false)
        mViewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        mViewModel.setView(this)
        activity?.let {
            recordFragmentListener = activity as? RecordFragmentListener
            mLinearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            mBinding.historyRv.layoutManager = mLinearLayoutManager

            mPaginator = object : Paginator(mLinearLayoutManager) {
                override fun paginate(nextPageIndex: Int) {
                    fetch(nextPageIndex)
                }
            }
            mBinding.historyRv.addOnScrollListener(mPaginator)
        }
        mId = arguments?.getString(E_TRANSACTION_ID) ?: ""

        mViewModel.history.observe(this) {
            updateUi(it.isNotEmpty())
            if (!::mHistoryAdapter.isInitialized) {
                mHistoryAdapter = HistoryAdapter(requireContext(), it)
                mBinding.historyRv.adapter = mHistoryAdapter
            } else {
                mHistoryAdapter.notifyWithData(it)
            }
        }
        fetch()
        return mBinding.root
    }

    fun fetch(nextPageIndex: Int = 1) {
        mViewModel.history.value?.clear()
        mViewModel.history(
            recordFragmentListener?.getType() ?: "",
            mId,
            nextPageIndex
        )
    }

    private fun updateUi(dataAvailable: Boolean) {
        mBinding.historyRv.visibility = if (dataAvailable) View.VISIBLE else View.GONE
        mBinding.noDataIv.visibility = if (dataAvailable) View.GONE else View.VISIBLE
    }
}