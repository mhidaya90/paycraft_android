package com.paycraft.ems.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paycraft.R
import com.paycraft.base.BaseBottomSheetDialogFragment
import com.paycraft.databinding.FragmentBottomSheetFilterPickerBinding
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.transactions.Filters
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterPickerBottomSheetFragment : BaseBottomSheetDialogFragment(), View.OnClickListener {

    companion object {
        private const val E_CARD_ID = "card_id"
        private const val E_TYPE = "type"
        const val E_FILTERS = "filters"
        fun start(
            fm: FragmentManager,
            type: String,
            cardId: String = "",
            filters: List<Filters>? = null
        ): FilterPickerBottomSheetFragment {
            val fragment = FilterPickerBottomSheetFragment()
            val b = Bundle()
            b.putString(E_TYPE, type)
            b.putString(E_CARD_ID, cardId)
            filters?.let {
                b.putParcelableArrayList(E_FILTERS, filters as ArrayList<Filters>)
            }
            fragment.arguments = b
            fragment.show(fm, OptionsBottomSheetFragment.TAG)
            return fragment
        }
    }

    private lateinit var mBinding: FragmentBottomSheetFilterPickerBinding
    private lateinit var mViewModel: FilterPickerBottomSheetViewModel

    private lateinit var mFilterPickerBottomSheetListener: FilterPickerBottomSheetListener

    private var mFiltersAdapter: FiltersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFilterPickerBottomSheetListener = requireActivity() as FilterPickerBottomSheetListener
        mViewModel = ViewModelProvider(this)[FilterPickerBottomSheetViewModel::class.java]
        mViewModel.setView(this)

        mViewModel.mCardId = arguments?.getString(E_CARD_ID)
        mViewModel.type = arguments?.getString(E_TYPE)
        val filters: List<Filters>? = arguments?.getParcelableArrayList(E_FILTERS)

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_filter_picker,
            container,
            false
        )
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED
        mBinding.clearTv.setOnClickListener(this)
        mBinding.applyTv.setOnClickListener(this)
        mBinding.closeFilterIv.setOnClickListener(this)

        mViewModel.mFilters.observe(this) {
            mFiltersAdapter = FiltersAdapter(it)
            mBinding.filtersRv.adapter = mFiltersAdapter
        }

        if (filters.isNullOrEmpty()) {
            mViewModel.filters()
        } else {
            mViewModel.mFilters.postValue(filters)
        }
        return mBinding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.closeFilterIv -> {
                dismiss()
            }

            R.id.clearTv -> {
                mFilterPickerBottomSheetListener.onApplyFilter(arrayListOf())
                dismiss()
            }

            R.id.applyTv -> {
                mViewModel.mFilters.value?.let {
                    mFilterPickerBottomSheetListener.onApplyFilter(
                        it
                    )
                }
                dismiss()
            }
        }
    }
}