package com.paycraft.ems

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.base.API_PATH_ADVANCES
import com.paycraft.base.API_PATH_REPORTS
import com.paycraft.base.API_PATH_TRANSACTIONS
import com.paycraft.base.API_PATH_TRIPS
import com.paycraft.base.BaseFragment
import com.paycraft.base.Paginator
import com.paycraft.base.SwipeToDeleteCallback
import com.paycraft.base.dialog
import com.paycraft.base.textChanges
import com.paycraft.base.toast
import com.paycraft.databinding.FragmentRecordsBinding
import com.paycraft.ems.advance.detail.AdvanceActivity
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.filter.FilterAdapter
import com.paycraft.ems.filter.FilterAdapterListener
import com.paycraft.ems.filter.FilterPickerBottomSheetFragment
import com.paycraft.ems.report_expenses.ReportExpensesFragmentListener
import com.paycraft.ems.reports.Report
import com.paycraft.ems.reports.detail.ReportActivity
import com.paycraft.ems.transactions.Filter
import com.paycraft.ems.transactions.Filters
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.transactions.TransactionsAdapter
import com.paycraft.ems.transactions.TransactionsAdapterListener
import com.paycraft.ems.transactions.detail.TransactionActivity
import com.paycraft.ems.trip.create.Trip
import com.paycraft.ems.trip.details.TripActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TransactionsFragment : BaseFragment(), OnClickListener, TransactionsAdapterListener,
    TransactionsView,
    FilterAdapterListener {
    companion object {
        var E_TAG = "tag"

        var TAG_EXPENSES = "Expenses"
        var TAG_TRANSACTIONS = "Transactions"
        var TAG_TRIPS = "Trips"
        var TAG_ADVANCES = "Advances"
        var TAG_REPORTS = "Reports"

        var E_REPORT_ID = "report_id"
        const val CARD_ID = "card_id"
        const val E_SEARCH_FILTER = "search_filter"
        fun newInstance(
            tag: String,
            cardId: String = "",
            reportId: String = "",
            isSearchAndFilterEnabled: Boolean = true
        ): TransactionsFragment {
            val args = Bundle()
            args.putString(E_TAG, tag)
            args.putString(CARD_ID, cardId)
            args.putString(E_REPORT_ID, reportId)
            args.putBoolean(E_SEARCH_FILTER, isSearchAndFilterEnabled)
            val fragment = TransactionsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mTransactionsAdapter: TransactionsAdapter
    lateinit var mBinding: FragmentRecordsBinding
    val mViewModel: TransactionsViewModel by viewModels()
    private var mPaginator: Paginator? = null
    private var mReportExpensesFragmentListener: ReportExpensesFragmentListener? = null

    private lateinit var mTripsAdapter: TransactionsAdapter
    private lateinit var mAdvancesAdapter: TransactionsAdapter
    private lateinit var mReportsAdapter: TransactionsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_records, container, false)
        mBinding.filterIv.setOnClickListener(this)
        mBinding.searchIv.setOnClickListener(this)
        mBinding.closeIv.setOnClickListener(this)

        mViewModel.setView(this)

        mViewModel.mCardId = arguments?.getString(CARD_ID) ?: ""
        mViewModel.mReportId = arguments?.getString(E_REPORT_ID) ?: ""
        mViewModel.mTag = arguments?.getString(E_TAG) ?: ""
        mViewModel.mOsSearchFilterEnabled = arguments?.getBoolean(E_SEARCH_FILTER) ?: true

        linearLayoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        mBinding.transactionsRv.layoutManager = linearLayoutManager


        if (!mViewModel.mOsSearchFilterEnabled) {
            mBinding.searchGp.visibility = GONE
            mBinding.filterIv.visibility = GONE
            mBinding.searchIv.visibility = GONE
            mBinding.selectedFiltersRv.visibility = GONE
        }

        updateSelectedFilters()
        setupPaging()
        setUpSwipeToDelete()
        setUpPullToRefresh()
        observeData()

        if (mViewModel.mReportId.isNotEmpty())
            mReportExpensesFragmentListener = activity as? ReportExpensesFragmentListener

        mBinding.searchEt.textChanges()
            .debounce(300)
            .onEach {
                mViewModel.mSearchKey = it.toString()
                refresh()
            }.launchIn(lifecycleScope)

        return mBinding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.closeIv -> {
                mBinding.searchGp.visibility = GONE
                mBinding.filterIv.visibility = VISIBLE
                mBinding.searchIv.visibility = VISIBLE
                mBinding.selectedFiltersRv.visibility = VISIBLE
                mBinding.searchEt.setText("")
            }

            R.id.searchIv -> {
                mBinding.searchGp.visibility = VISIBLE
                mBinding.filterIv.visibility = GONE
                mBinding.searchIv.visibility = INVISIBLE
                mBinding.selectedFiltersRv.visibility = GONE
            }

            R.id.filterIv -> {
                FilterPickerBottomSheetFragment.start(
                    childFragmentManager,
                    when (mViewModel.mTag) {
                        TAG_REPORTS -> API_PATH_REPORTS
                        TAG_ADVANCES -> API_PATH_ADVANCES
                        TAG_TRIPS -> API_PATH_TRIPS
                        else -> API_PATH_TRANSACTIONS
                    },
                    mViewModel.mCardId,
                    mViewModel.mSelectedFilters
                )
            }
        }
    }

    override fun onTransactionClicked(transaction: Transaction) {
        if (mViewModel.mReportId.isNotEmpty()) return
        activity?.let { transaction.id?.let { it1 -> TransactionActivity.start(it, it1) } }
    }

    override fun isSelected(transaction: Transaction): Boolean {
        return false
    }

    override fun onDeleteTransactionSuccess() {
        mViewModel.mTransactions.value?.let {
            it.remove(mViewModel.mDeleteConfirmationTransaction)
            mBinding.transactionsRv.recycledViewPool.clear()
            mTransactionsAdapter.notifyWithData(it)
        }
    }

    override fun onReportClicked(report: Report) {
        report.id?.let {
            ReportActivity.start(requireActivity(), it)
        }
    }

    override fun onDeleteReportSuccess() {
        mViewModel.mReports.value?.let {
            it.remove(mViewModel.mDeleteConfirmationReport)
            mBinding.transactionsRv.recycledViewPool.clear()
            mReportsAdapter.notifyWithData(it)
        }
    }

    override fun onDeleteTripSuccess() {
        mViewModel.mTrips.value?.let {
            it.remove(mViewModel.mDeleteConfirmationTrip)
            mBinding.transactionsRv.recycledViewPool.clear()
            mTripsAdapter.notifyWithData(it)
        }
    }

    override fun onDeleteAdvanceSuccess() {
        mViewModel.mAdvances.value?.let {
            it.remove(mViewModel.mDeleteConfirmationAdvance)
            mBinding.transactionsRv.recycledViewPool.clear()
            mAdvancesAdapter.notifyWithData(it)
        }
    }

    override fun onTripClicked(trip: Trip) {
        if (mViewModel.mReportId.isNotEmpty()) return
        trip.id?.let {
            TripActivity.start(requireActivity(), it)
        }
    }

    override fun isSelected(advance: Trip): Boolean {
        return false
    }

    override fun onAdvanceClicked(advance: Advance) {
        if (mViewModel.mReportId.isNotEmpty()) return
        advance.id?.let {
            AdvanceActivity.start(requireActivity(), it)
        }
    }

    override fun isSelected(transaction: Advance): Boolean {
        return false
    }

    fun onAdvanceLinkedSuccessfully() {
        refresh()
    }

    fun onTripLinkedSuccessfully() {
        refresh()
    }

    fun onApplyFilter(list: List<Filters>) {
        mViewModel.mSelectedFilters = list
        refresh()
    }

    private fun clearOldRecords() {
        updateSelectedFilters()
        mPaginator?.reset()
        deleteRecords()
    }

    private fun deleteRecords() {
        when (mViewModel.mTag) {
            TAG_REPORTS -> {
                mViewModel.mReports.value?.clear()
                mViewModel.mReports.value?.let {
                    mReportsAdapter.notifyWithData(it)
                }
            }

            TAG_ADVANCES -> {
                mViewModel.mAdvances.value?.clear()
                mViewModel.mAdvances.value?.let {
                    mAdvancesAdapter.notifyWithData(it)
                }
            }

            TAG_TRIPS -> {
                mViewModel.mTrips.value?.clear()
                mViewModel.mTrips.value?.let {
                    mTripsAdapter.notifyWithData(it)
                }
            }

            else -> {
                mViewModel.mTransactions.value?.clear()
                mViewModel.mTransactions.value?.let {
                    mTransactionsAdapter.notifyWithData(it)
                }
            }
        }
    }

    private fun updateSelectedFilters() {
        mViewModel.filtersMapString()?.let {
            mBinding.selectedFiltersRv.adapter = FilterAdapter(it, false, this)
        }
    }

    private fun doApi(page: Int = 0) {
        when (mViewModel.mTag) {
            TAG_REPORTS -> {
                mViewModel.reports(page)
            }

            TAG_TRIPS -> {
                mViewModel.getTrips(page)
            }

            TAG_ADVANCES -> {
                mViewModel.getAdvances(page)
            }

            else -> {
                mViewModel.transactions(page)
            }
        }
    }

    private fun setupPaging() {
        mPaginator = object : Paginator(linearLayoutManager) {
            override fun paginate(nextPageIndex: Int) {
                doApi(nextPageIndex)
            }
        }
        mPaginator?.let {
            mBinding.transactionsRv.addOnScrollListener(it)
        }
    }

    private fun setUpSwipeToDelete() {
        context?.let {
            //swipe to delete
            ItemTouchHelper(SwipeToDeleteCallback(context) { position ->
                when (mViewModel.mTag) {
                    TAG_REPORTS -> {
                        mViewModel.mDeleteConfirmationReport =
                            mViewModel.mReports.value?.get(position)
                        mViewModel.mReports.value?.removeAt(position)
                        mReportsAdapter.notifyItemRemoved(position)
                        mViewModel.mReports.value?.add(
                            position,
                            mViewModel.mDeleteConfirmationReport!!
                        )
                        mReportsAdapter.notifyItemInserted(position)
                        if (mViewModel.mDeleteConfirmationReport?.canDelete() == false) {
                            toast("Can't Delete Report!")
                            mViewModel.mDeleteConfirmationReport = null
                            return@SwipeToDeleteCallback
                        }
                        dialog(
                            getString(R.string.delete_confirmation),
                            getString(R.string.dialog_yes),
                            getString(R.string.dialog_no)
                        ) { _, _ ->
                            mViewModel.mDeleteConfirmationReport?.id?.let { tId ->
                                mViewModel.deleteReport(tId)
                            }
                        }
                    }

                    TAG_TRIPS -> {
                        //hold
                        mViewModel.mDeleteConfirmationTrip = mViewModel.mTrips.value?.get(position)
                        //remove
                        mViewModel.mTrips.value?.removeAt(position)
                        mTripsAdapter.notifyItemRemoved(position)
                        //add
                        mViewModel.mTrips.value?.add(
                            position,
                            mViewModel.mDeleteConfirmationTrip!!
                        )
                        mTripsAdapter.notifyItemInserted(position)

                        if (mViewModel.mReportId.isEmpty()) {
                            //get confirmation
                            if (mViewModel.mDeleteConfirmationTrip?.canDelete() == false) {
                                toast("Can't Delete Trip!")
                                mViewModel.mDeleteConfirmationTrip = null
                                return@SwipeToDeleteCallback
                            }
                            dialog(
                                getString(R.string.delete_confirmation),
                                getString(R.string.dialog_yes),
                                getString(R.string.dialog_no)
                            ) { _, _ ->
                                mViewModel.mDeleteConfirmationTrip?.id?.let { tId ->
                                    mViewModel.deleteTrip(tId)
                                }
                            }
                        } else {
                            if (mReportExpensesFragmentListener?.canUnlink() != true) {
                                toast("Can't Unlink Trip!")
                                mViewModel.mDeleteConfirmationTrip = null
                                return@SwipeToDeleteCallback
                            }
                            //get confirmation
                            dialog(
                                getString(R.string.unlink_confirmation),
                                getString(R.string.dialog_yes),
                                getString(R.string.dialog_no)
                            ) { _, _ ->
                                mViewModel.unlintTrip()
                            }
                        }
                    }

                    TAG_ADVANCES -> {
                        //hold
                        mViewModel.mDeleteConfirmationAdvance =
                            mViewModel.mAdvances.value?.get(position)
                        //remove
                        mViewModel.mAdvances.value?.removeAt(position)
                        mAdvancesAdapter.notifyItemRemoved(position)
                        //add
                        mViewModel.mAdvances.value?.add(
                            position,
                            mViewModel.mDeleteConfirmationAdvance!!
                        )
                        mAdvancesAdapter.notifyItemInserted(position)
                        if (mViewModel.mReportId.isEmpty()) {
                            //get confirmation
                            if (mViewModel.mDeleteConfirmationAdvance?.canDelete() == false) {
                                toast("Can't Delete Advance!")
                                mViewModel.mDeleteConfirmationAdvance = null
                                return@SwipeToDeleteCallback
                            }
                            dialog(
                                getString(R.string.delete_confirmation),
                                getString(R.string.dialog_yes),
                                getString(R.string.dialog_no)
                            ) { _, _ ->
                                mViewModel.mDeleteConfirmationAdvance?.id?.let { tId ->
                                    mViewModel.deleteAdvance(tId)
                                }
                            }
                        } else {
                            if (mReportExpensesFragmentListener?.canUnlink() != true) {
                                toast("Can't Unlink Advance!")
                                mViewModel.mDeleteConfirmationAdvance = null
                                return@SwipeToDeleteCallback
                            }
                            //get confirmation
                            dialog(
                                getString(R.string.unlink_confirmation),
                                getString(R.string.dialog_yes),
                                getString(R.string.dialog_no)
                            ) { _, _ ->
                                mViewModel.unlintAdvance()
                            }
                        }
                    }

                    else -> {
                        //hold
                        mViewModel.mDeleteConfirmationTransaction =
                            mViewModel.mTransactions.value?.get(position)
                        //remove
                        mViewModel.mTransactions.value?.removeAt(position)
                        mTransactionsAdapter.notifyItemRemoved(position)
                        //add
                        mViewModel.mTransactions.value?.add(
                            position,
                            mViewModel.mDeleteConfirmationTransaction!!
                        )
                        mTransactionsAdapter.notifyItemInserted(position)
                        if (mViewModel.mReportId.isEmpty()) {
                            if (mViewModel.mDeleteConfirmationTransaction?.isCardTransaction() == true) {
                                toast("Can't Delete Card Transaction!")
                                return@SwipeToDeleteCallback
                            }
                            if (mViewModel.mDeleteConfirmationTransaction?.canDelete() == false) {
                                toast("Can't Delete Transaction!")
                                mViewModel.mDeleteConfirmationTransaction = null
                                return@SwipeToDeleteCallback
                            }
                            //get confirmation
                            dialog(
                                getString(R.string.delete_confirmation),
                                getString(R.string.dialog_yes),
                                getString(R.string.dialog_no)
                            ) { _, _ ->
                                mViewModel.deleteTransaction()
                            }
                        } else {
                            if (mReportExpensesFragmentListener?.canUnlink() != true) {
                                toast("Can't Unlink Transaction!")
                                mViewModel.mDeleteConfirmationTransaction = null
                                return@SwipeToDeleteCallback
                            }
                            //get confirmation
                            dialog(
                                getString(R.string.unlink_confirmation),
                                getString(R.string.dialog_yes),
                                getString(R.string.dialog_no)
                            ) { _, _ ->
                                mViewModel.unlintExpense()
                            }
                        }

                    }
                }
            }).attachToRecyclerView(mBinding.transactionsRv)

        }
    }

    private fun setUpPullToRefresh() {
        //pull refresh
        mBinding.transactionsSrl.setOnRefreshListener {
            mViewModel.apiRefresh = true
            refresh()
            mBinding.transactionsSrl.isRefreshing = false
        }
    }

    private fun updateUi(dataAvailable: Boolean) {
        mBinding.transactionsRv.visibility = if (dataAvailable) VISIBLE else GONE
        mBinding.noDataIv.visibility = if (dataAvailable) GONE else VISIBLE
    }

    fun onTransactionsLinkedSuccess() {
        refresh()
    }

    override fun onUnLinkTransactionsSuccess() {
        mReportExpensesFragmentListener?.onTransactionUnLined()
    }

    override fun onUnLinkTripSuccess() {
        refresh()
    }

    override fun onUnLinkAdvanceSuccess() {
        refresh()
    }

    private fun observeData() {
        mViewModel.mTransactions.observe(viewLifecycleOwner) {
            it?.let { data ->
                if (!::mTransactionsAdapter.isInitialized) {
                    mTransactionsAdapter = TransactionsAdapter(
                        requireContext(),
                        R.layout.row_expense, this, data
                    )
                    mBinding.transactionsRv.adapter = mTransactionsAdapter
                }
                mBinding.transactionsRv.recycledViewPool.clear()
                mTransactionsAdapter.notifyWithData(data)
                mPaginator?.pageLoadingCompleted(
                    mViewModel.mTotalRecords,
                    data.size
                )
                updateUi(data.isNotEmpty())
            } ?: run {
                updateUi(false)
            }
            if (mViewModel.apiRefresh) {
                (activity as TransactionsFragmentListener).onReload()
                mViewModel.apiRefresh = false
            }
        }
        mViewModel.mReports.observe(viewLifecycleOwner) {
            it?.let { data ->
                if (!::mTransactionsAdapter.isInitialized) {
                    mReportsAdapter = TransactionsAdapter(
                        requireContext(), R.layout.row_report,
                        this, data
                    )
                    mBinding.transactionsRv.adapter = mReportsAdapter
                } else {
                    mBinding.transactionsRv.recycledViewPool.clear()
                    mReportsAdapter.notifyWithData(data)
                }
                mPaginator?.pageLoadingCompleted(
                    mViewModel.mTotalRecords,
                    data.size
                )
                updateUi(data.isNotEmpty())
            } ?: run {
                updateUi(false)
            }
        }
        mViewModel.mAdvances.observe(viewLifecycleOwner) {
            it?.let { data ->
                if (!::mTransactionsAdapter.isInitialized) {
                    mAdvancesAdapter =
                        TransactionsAdapter(requireContext(), R.layout.row_advance, this, data)
                    mBinding.transactionsRv.adapter = mAdvancesAdapter
                } else {
                    mBinding.transactionsRv.recycledViewPool.clear()
                    mAdvancesAdapter.notifyWithData(data)
                }
                mPaginator?.pageLoadingCompleted(
                    mViewModel.mTotalRecords,
                    data.size
                )
                updateUi(data.isNotEmpty())
            } ?: run {
                updateUi(false)
            }
        }
        mViewModel.mTrips.observe(viewLifecycleOwner) {
            it?.let { data ->
                if (!::mTransactionsAdapter.isInitialized) {
                    mTripsAdapter =
                        TransactionsAdapter(requireContext(), R.layout.row_trip, this, data)
                    mBinding.transactionsRv.adapter = mTripsAdapter
                } else {
                    mBinding.transactionsRv.recycledViewPool.clear()
                    mTripsAdapter.notifyWithData(data)
                }
                mPaginator?.pageLoadingCompleted(
                    mViewModel.mTotalRecords,
                    data.size
                )
                updateUi(data.size > 0)
            } ?: run {
                updateUi(false)
            }
        }
    }

    fun refresh() {
        clearOldRecords()
        doApi()
    }

    fun reload() {
        mViewModel.apiRefresh = true
        refresh()
    }

    override fun onRemoveFilter(filter: Filter) {
        mViewModel.unselectFilter(filter)
        mPaginator?.reset()
        deleteRecords()
        doApi()
    }
}
