package com.paycraft.ems.transaction_picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.paycraft.R
import com.paycraft.base.BaseBottomSheetDialogFragment
import com.paycraft.base.BaseView
import com.paycraft.base.toast
import com.paycraft.databinding.FragmentBottomSheetExpensePickerBinding
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.transactions.TransactionsAdapter
import com.paycraft.ems.transactions.TransactionsAdapterListener
import com.paycraft.ems.trip.create.Trip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordPickerBottomFragment : BaseBottomSheetDialogFragment(),
    TransactionsAdapterListener, View.OnClickListener, BaseView {
    companion object {
        const val E_TYPE = "type"
        const val TAG_TRANSACTIONS = "Include Transactions"
        const val TAG_ADVANCES = "Manage Advances"
        const val TAG_TRIPS = "Manage Trips"
        fun start(
            fm: FragmentManager,
            type: String
        ): RecordPickerBottomFragment {
            val fragment = RecordPickerBottomFragment()
            val bundle = Bundle()
            bundle.putString(E_TYPE, type)
            fragment.arguments = bundle
            fragment.show(fm, OptionsBottomSheetFragment.TAG)
            return fragment
        }
    }

    lateinit var mViewModel: ExpensePickerBottomViewModel

    private var mExpensePickerBottomFragmentListener: RecordPickerBottomFragmentListener? = null
    private lateinit var mTransactionsAdapter: TransactionsAdapter
    private lateinit var mAdvancesAdapter: TransactionsAdapter
    private lateinit var mTripsAdapter: TransactionsAdapter

    lateinit var mBinding: FragmentBottomSheetExpensePickerBinding
    var mType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_expense_picker,
            container,
            false
        )
        mExpensePickerBottomFragmentListener = activity as? RecordPickerBottomFragmentListener

        mViewModel = ViewModelProvider(this)[ExpensePickerBottomViewModel::class.java]
        mViewModel.setView(this)

        mType = arguments?.getString(E_TYPE)
        mBinding.bottomSheetTitleTv.text = mType

        mBinding.includeTransactionsTv.setOnClickListener(this)
        mViewModel.mLocalErrors.observe(this) {
            if (it.showMessage)
                toast(it.message)
            if (it.navigateBack)
                dismiss()
        }
        mViewModel.mTransactions.observe(this) {
            if (mViewModel.mTransactionsList.isEmpty()) {
                toast("No un-reported Transactions found!")
                dismiss()
            }
            mTransactionsAdapter.notifyWithData(mViewModel.mTransactionsList)
        }
        mViewModel.mTrips.observe(this) {
            if (mViewModel.mTripsList.isEmpty()) {
                toast("No Approved Trips found!")
                dismiss()
            }
            mTripsAdapter.notifyWithData(mViewModel.mTripsList)
        }
        mViewModel.mAdvances.observe(this) {
            if (mViewModel.mAdvancesList.isEmpty()) {
                toast("No Approved Advances found!")
                dismiss()
            }
            mAdvancesAdapter.notifyWithData(mViewModel.mAdvancesList)
        }
        when (mType) {
            TAG_TRANSACTIONS -> {
                mBinding.includeTransactionsTv.text = "Include Transactions"
                context?.let {
                    mTransactionsAdapter =
                        TransactionsAdapter(
                            it,
                            R.layout.row_expense,
                            this,
                            mViewModel.mTransactionsList
                        )
                    mBinding.transactionsRv.adapter = mTransactionsAdapter
                }
                mViewModel.transactions()
            }

            TAG_ADVANCES -> {
                mBinding.includeTransactionsTv.text = "Include Advances"
                context?.let {
                    mAdvancesAdapter =
                        TransactionsAdapter(
                            it,
                            R.layout.row_advance,
                            this,
                            mViewModel.mAdvancesList
                        )
                    mBinding.transactionsRv.adapter = mAdvancesAdapter
                }
                mViewModel.getAdvances()
            }

            TAG_TRIPS -> {
                mBinding.includeTransactionsTv.text = "Include Trips"
                context?.let {
                    mTripsAdapter =
                        TransactionsAdapter(it, R.layout.row_trip, this, mViewModel.mTripsList)
                    mBinding.transactionsRv.adapter = mTripsAdapter
                }
                mViewModel.getTrips()
            }
        }
        return mBinding.root
    }

    override fun onClick(v: View?) {
        when (mType) {
            TAG_TRANSACTIONS -> {
                mExpensePickerBottomFragmentListener?.onTransactionsSelected(mViewModel.mTransactionsSelected)
            }

            TAG_ADVANCES -> {
                mExpensePickerBottomFragmentListener?.onAdvancesSelected(mViewModel.mAdvancesSelected)
            }

            TAG_TRIPS -> {
                mExpensePickerBottomFragmentListener?.onTripsSelected(mViewModel.mTripsSelected)
            }
        }
        dismiss()
    }

    override fun onTransactionClicked(transaction: Transaction) {
        if (mViewModel.mTransactionsSelected.contains(transaction))
            mViewModel.mTransactionsSelected.remove(transaction)
        else
            mViewModel.mTransactionsSelected.add(transaction)
        mTransactionsAdapter.notifyWithData(mViewModel.mTransactionsList)
    }

    override fun isSelected(transaction: Transaction): Boolean {
        return mViewModel.mTransactionsSelected.contains(transaction)
    }

    override fun onAdvanceClicked(advance: Advance) {
        if (mViewModel.mAdvancesSelected.contains(advance))
            mViewModel.mAdvancesSelected.remove(advance)
        else
            mViewModel.mAdvancesSelected.add(advance)
        mAdvancesAdapter.notifyWithData(mViewModel.mAdvancesList)
    }

    override fun isSelected(advance: Advance): Boolean {
        return mViewModel.mAdvancesSelected.contains(advance)
    }

    override fun onTripClicked(trip: Trip) {
        if (mViewModel.mTripsSelected.contains(trip))
            mViewModel.mTripsSelected.remove(trip)
        else
            mViewModel.mTripsSelected.add(trip)
        mTripsAdapter.notifyWithData(mViewModel.mTripsList)
    }

    override fun isSelected(trip: Trip): Boolean {
        return mViewModel.mTripsSelected.contains(trip)
    }
}