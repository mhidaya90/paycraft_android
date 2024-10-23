package com.paycraft.ems.report_picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.base.BaseBottomSheetDialogFragment
import com.paycraft.databinding.FragmentBottomSheetReportPickerBinding
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.reports.Report
import com.paycraft.ems.transactions.TransactionsAdapter
import com.paycraft.ems.transactions.TransactionsAdapterListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportPickerBottomFragment : BaseBottomSheetDialogFragment(),
    TransactionsAdapterListener {
    companion object {
        private const val E_REPORTS = "reports"
        fun start(
            fm: FragmentManager,
            list: List<Report>
        ): ReportPickerBottomFragment {
            val fragment = ReportPickerBottomFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(E_REPORTS, list as ArrayList<Report>)
            fragment.arguments = bundle
            fragment.show(fm, OptionsBottomSheetFragment.TAG)
            return fragment
        }
    }

    private var mExpensePickerBottomFragmentListener: ReportPickerBottomFragmentListener? = null
    private lateinit var mReportsAdapter: TransactionsAdapter
    lateinit var mBinding: FragmentBottomSheetReportPickerBinding
    val mList = arrayListOf<Report>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_report_picker,
            container,
            false
        )
        arguments?.getParcelableArrayList<Report>(E_REPORTS)?.let { mList.addAll(it) }
        context?.let {
            mReportsAdapter = TransactionsAdapter(it, R.layout.row_report, this, mList)
            mBinding.reportRv.adapter = mReportsAdapter
            mBinding.reportRv.layoutManager =
                LinearLayoutManager(it, RecyclerView.VERTICAL, false)
        }
        mExpensePickerBottomFragmentListener = activity as? ReportPickerBottomFragmentListener
        if (mList.isEmpty()) {
            mBinding.bottomSheetTitleTv.setText(R.string.no_report)
            mBinding.noDataIv.visibility = View.VISIBLE
            mBinding.reportRv.visibility = View.GONE
        } else {
            mBinding.bottomSheetTitleTv.setText(R.string.select_report)
            mBinding.noDataIv.visibility = View.GONE
            mBinding.reportRv.visibility = View.VISIBLE
        }
        return mBinding.root
    }

    override fun onReportClicked(report: Report) {
        mExpensePickerBottomFragmentListener?.onReportSelected(report)
        dismiss()
    }
}