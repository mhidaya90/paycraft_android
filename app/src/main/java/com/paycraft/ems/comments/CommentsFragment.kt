package com.paycraft.ems.comments

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
import com.paycraft.databinding.FragmentCommentsBinding
import com.paycraft.ems.transactions.detail.RecordFragmentListener
import com.paycraft.ems.transactions.detail.TransactionActivity.Companion.E_TRANSACTION_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : BaseFragment(), CommentsView {
    companion object {
        const val TAG = "Comments"
        fun newInstance(id: String?): CommentsFragment {
            val args = Bundle()
            args.putString(E_TRANSACTION_ID, id)
            val fragment = CommentsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var mBinding: FragmentCommentsBinding
    private lateinit var mCommentsAdapter: CommentsAdapter
    lateinit var mCommentsViewModel: CommentsViewModel
    lateinit var mId: String
    private lateinit var mPaginator: Paginator
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private var recordFragmentListener: RecordFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_comments, container, false)
        mCommentsViewModel = ViewModelProvider(this)[CommentsViewModel::class.java]
        mCommentsViewModel.setView(this)
        activity?.let {
            recordFragmentListener = activity as? RecordFragmentListener
            mLinearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            mBinding.commentRv.layoutManager = mLinearLayoutManager
            mPaginator = object : Paginator(mLinearLayoutManager) {
                override fun paginate(nextPageIndex: Int) {
                    fetch(nextPageIndex)
                }
            }
            mBinding.commentRv.addOnScrollListener(mPaginator)
        }
        mId = arguments?.getString(E_TRANSACTION_ID, "") ?: ""

        mCommentsViewModel.comments.observe(this) {
            updateUi(it.isNotEmpty())
            if (!::mCommentsAdapter.isInitialized) {
                mCommentsAdapter = CommentsAdapter(requireContext(), it)
                mBinding.commentRv.adapter = mCommentsAdapter
            } else {
                mCommentsAdapter.notifyWithData(it)
            }

        }
        fetch()
        return mBinding.root
    }

    fun fetch(nextPageIndex: Int = 1) {
        mCommentsViewModel.comments.value?.clear()
        mCommentsViewModel.comments(
            recordFragmentListener?.getType() ?: "",
            mId,
            nextPageIndex
        )
    }

    private fun updateUi(dataAvailable: Boolean) {
        mBinding.commentRv.visibility = if (dataAvailable) View.VISIBLE else View.GONE
        mBinding.noDataIv.visibility = if (dataAvailable) View.GONE else View.VISIBLE
    }
}