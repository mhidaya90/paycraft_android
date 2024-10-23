package com.paycraft.ems.advance_picker

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
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.transactions.TransactionsAdapter
import com.paycraft.ems.transactions.TransactionsAdapterListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdvancePickerBottomFragment : BaseBottomSheetDialogFragment(),
    TransactionsAdapterListener {
    companion object {
        private const val E_REPORTS = "reports"
        fun start(
            fm: FragmentManager,
            list: List<Advance>
        ): AdvancePickerBottomFragment {
            val fragment = AdvancePickerBottomFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(E_REPORTS, list as ArrayList<Advance>)
            fragment.arguments = bundle
            fragment.show(fm, OptionsBottomSheetFragment.TAG)
            return fragment
        }
    }

    private var mExpensePickerBottomFragmentListener: AdvancePickerBottomFragmentListener? = null
    private lateinit var mAdvancesAdapter: TransactionsAdapter
    lateinit var mBinding: FragmentBottomSheetReportPickerBinding
    val mList = arrayListOf<Advance>()
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
        arguments?.getParcelableArrayList<Advance>(E_REPORTS)?.let { mList.addAll(it) }
        context?.let {
            mAdvancesAdapter = TransactionsAdapter(it, R.layout.row_advance, this, mList)
            mBinding.reportRv.adapter = mAdvancesAdapter
            mBinding.reportRv.layoutManager =
                LinearLayoutManager(it, RecyclerView.VERTICAL, false)
        }
        mExpensePickerBottomFragmentListener = activity as? AdvancePickerBottomFragmentListener
        return mBinding.root
    }

    override fun onAdvanceClicked(advance: Advance) {
        mExpensePickerBottomFragmentListener?.onAdvanceSelected(advance)
        dismiss()
    }

    override fun isSelected(advance: Advance): Boolean {
        return advance.isSelected ?: false
    }
}