package com.paycraft.ems.reports.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.paycraft.R
import com.paycraft.base.ViewPagerAdapter
import com.paycraft.base.dialog
import com.paycraft.base.toast
import com.paycraft.databinding.ActivityReportBinding
import com.paycraft.ems.PolicyViolationsBottomSheetFragmentListener
import com.paycraft.ems.Record
import com.paycraft.ems.RecordStatus.Companion.STATUS_PENDING_APPROVAL
import com.paycraft.ems.RecordStatus.Companion.STATUS_PENDING_REIMBURSEMENT
import com.paycraft.ems.RecordStatus.Companion.STATUS_REIMBURSED
import com.paycraft.ems.RecordStatus.Companion.STATUS_REJECTED
import com.paycraft.ems.RecordStatus.Companion.STATUS_UN_SUBMITTED
import com.paycraft.ems.TransactionsFragment
import com.paycraft.ems.TransactionsFragmentListener
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.comments.CommentsFragment
import com.paycraft.ems.comments.CreateCommentBottomSheetFragment
import com.paycraft.ems.comments.CreateCommentBottomSheetListener
import com.paycraft.ems.filter.FilterPickerBottomSheetListener
import com.paycraft.ems.history.HistoryFragment
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.options_picker.OptionsListener
import com.paycraft.ems.report_expenses.ReportExpensesFragmentListener
import com.paycraft.ems.reports.PolicyViolationsBottomSheetFragment
import com.paycraft.ems.reports.Report
import com.paycraft.ems.reports.create.CreateReportActivity
import com.paycraft.ems.transaction_picker.LinkTransactionsToRequest
import com.paycraft.ems.transaction_picker.RecordPickerBottomFragment
import com.paycraft.ems.transaction_picker.RecordPickerBottomFragment.Companion.TAG_ADVANCES
import com.paycraft.ems.transaction_picker.RecordPickerBottomFragment.Companion.TAG_TRANSACTIONS
import com.paycraft.ems.transaction_picker.RecordPickerBottomFragment.Companion.TAG_TRIPS
import com.paycraft.ems.transaction_picker.RecordPickerBottomFragmentListener
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.Filters
import com.paycraft.ems.transactions.RecordActivity
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.transactions.create.CreateTransactionActivity
import com.paycraft.ems.transactions.detail.DetailsFragment
import com.paycraft.ems.transactions.detail.RecordFragmentListener
import com.paycraft.ems.transactions.detail.TransactionActivity.Companion.E_TRANSACTION_ID
import com.paycraft.ems.trip.create.Trip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportActivity : RecordActivity(),
    ReportView, OptionsListener, RecordPickerBottomFragmentListener, View.OnClickListener,
    CreateCommentBottomSheetListener, ReportExpensesFragmentListener,
    FilterPickerBottomSheetListener, PolicyViolationsBottomSheetFragmentListener,
    TransactionsFragmentListener, RecordFragmentListener {
    companion object {
        const val E_REPORT_ID = "report_id"
        fun start(activity: Activity, id: String) {
            Intent(activity, ReportActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.putExtra(E_REPORT_ID, id)
                }.run {
                    activity.startActivityForResult(this, RC_REFRESH_PREVIOUS_SCREEN)
                }
        }
    }

    private lateinit var mViewModel: ReportViewModel
    private lateinit var mBinding: ActivityReportBinding
    private lateinit var mTransactionsFragment: TransactionsFragment
    private lateinit var mAdvancesFragment: TransactionsFragment
    private lateinit var mTripsFragment: TransactionsFragment
    private lateinit var mCommentsFragment: CommentsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getStringExtra(E_REPORT_ID)
        if (id.isNullOrEmpty()) {
            toast("Report Trip!")
            finish()
            return
        }
        mViewModel = ViewModelProvider(this)[ReportViewModel::class.java]
        mViewModel.setView(this)
        mViewModel.mId = intent.getStringExtra(E_REPORT_ID) ?: ""

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_report)
        configToolbar(
            mBinding.toolbar,
            mBinding.toolbarTitleTv,
            getString(R.string.title_report)
        )
        mBinding.moreIv.setOnClickListener(this)
        mBinding.editTv.setOnClickListener(this)
        mBinding.submitTv.setOnClickListener(this)
        mBinding.recallTv.setOnClickListener(this)
        mBinding.viewViolationsTv.setOnClickListener(this)

        mViewModel.mViolations.observe(this) { v ->
            PolicyViolationsBottomSheetFragment.start(
                supportFragmentManager,
                v as ArrayList<DisplayViolation>
            )
        }
        mViewModel.getReport()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_CANCELED == resultCode) return
        when (requestCode) {
            RC_REFRESH_PREVIOUS_SCREEN -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                if (!refreshPreviousScreen) return
                mTransactionsFragment.refresh()
            }

            CreateTransactionActivity.RC -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                val transactionId = data?.getStringExtra(E_TRANSACTION_ID)
                mViewModel.mReport.id?.let { rId ->
                    transactionId?.let { tId ->
                        mViewModel.getTransaction(tId, rId)
                    }
                }
            }

            CreateReportActivity.RC -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                mViewModel.getReport()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.viewViolationsTv -> {
                PolicyViolationsBottomSheetFragment.start(
                    supportFragmentManager,
                    mViewModel.mReport.expenseErrors?.getViolationList()
                )
            }

            R.id.recallTv -> {
                mViewModel.mReport.id?.let { mViewModel.recallReport(it) }
            }

            R.id.submitTv -> {
                mViewModel.isSubmit = true
                mViewModel.validateTransaction()
            }

            R.id.moreIv -> {
                val options = mutableListOf<FieldOption>()
                if (mViewModel.mReport.canEdit()) {
                    options.add(FieldOption("include_expenses", "Include Expenses"))
                    options.add(FieldOption("add_expenses", "Add Expenses"))
                    options.add(FieldOption("manage_advance", "Manage Advance"))
                    options.add(FieldOption("manage_trips", "Manage Trips"))
                }
                options.add(FieldOption("add_comments", "Add Comments"))
                if (mViewModel.mReport.canDelete()) {
                    options.add(FieldOption("delete", "Delete"))
                }
                if (options.isEmpty()) return
                OptionsBottomSheetFragment.start(
                    supportFragmentManager,
                    options = options as ArrayList<FieldOption>
                )
            }

            R.id.editTv -> {
                mViewModel.mReport.id?.let { CreateReportActivity.start(this, it) }
            }
        }
    }

    override fun onOptionSelected(type: String, fieldOption: FieldOption) {
        when (fieldOption.id) {
            "manage_trips" -> {
                RecordPickerBottomFragment.start(supportFragmentManager, TAG_TRIPS)
            }

            "manage_advance" -> {
                RecordPickerBottomFragment.start(supportFragmentManager, TAG_ADVANCES)
            }

            "include_expenses" -> {
                RecordPickerBottomFragment.start(supportFragmentManager, TAG_TRANSACTIONS)
            }

            "add_expenses" -> {
                CreateTransactionActivity.start(this)
            }

            "add_comments" -> {
                CreateCommentBottomSheetFragment.start(supportFragmentManager)
            }

            "delete" -> {
                dialog(
                    getString(R.string.delete_confirmation),
                    getString(R.string.dialog_yes),
                    getString(R.string.dialog_no)
                ) { _, _ ->
                    mViewModel.mReport.id?.let {
                        mViewModel.deleteReport(it)
                    }
                }
            }
        }
    }

    override fun onSubmitComment(comment: String) {
        mViewModel.mReport.id?.let {
            mViewModel.createReportComment(it, comment)
        }
    }

    override fun onTransactionsSelected(transactions: List<Transaction>) {
        mViewModel.isSubmit = false
        mViewModel.mSelectedTransactionsToInclude.clear()
        mViewModel.mSelectedTransactionsToInclude.addAll(transactions)
        mViewModel.validateTransaction()
    }

    override fun onTripsSelected(trips: List<Trip>) {
        mViewModel.linkTripToReport(trips)
    }

    override fun onAdvancesSelected(trips: List<Advance>) {
        mViewModel.linkAdvanceToReport(trips)
    }

    override fun reportSuccess(report: Report) {
        mViewModel.mReport = report
        mBinding.expenseIdValueTv.text = mViewModel.mReport.number ?: ""
        mBinding.reportTitleTv.text = mViewModel.mReport.reportTitle ?: "-"
        "${mViewModel.mReport.fromDate ?: "-"} - ${mViewModel.mReport.toDate ?: "-"}".also {
            mBinding.startEndDateTv.text = it
        }
        mBinding.reportStatusTv.text = mViewModel.mReport.displayStatus()
        mBinding.amountTv.text = mViewModel.mReport.displayAmount()
        statusColors(
            this,
            report.status ?: "",
            mBinding.expenseIndicatorView,
            mBinding.reportStatusTv
        )
        buildReportTabs()

        when (report.status) {
            STATUS_REJECTED,
            STATUS_PENDING_REIMBURSEMENT,
            STATUS_REIMBURSED -> {
                mBinding.editTv.visibility = GONE
                mBinding.submitTv.visibility = GONE
                mBinding.recallTv.visibility = GONE
            }

            STATUS_UN_SUBMITTED -> {
                mBinding.editTv.visibility = VISIBLE
                mBinding.submitTv.visibility = VISIBLE
                mBinding.recallTv.visibility = GONE
            }

            STATUS_PENDING_APPROVAL -> {
                mBinding.editTv.visibility = GONE
                mBinding.submitTv.visibility = GONE
                mBinding.recallTv.visibility = if (report.recallable == true) VISIBLE else GONE
            }

            else -> {
                mBinding.editTv.visibility = VISIBLE
                mBinding.submitTv.visibility = VISIBLE
                mBinding.recallTv.visibility = GONE
            }
        }
        mBinding.policyViolationsGp.visibility =
            if (report.expenseErrors?.getViolationList().isNullOrEmpty())
                GONE
            else
                VISIBLE
    }

    override fun onTransactionSuccess(transaction: Transaction, rId: String) {
        transaction.id?.let { tId ->
            mViewModel.isSubmit = false
            mViewModel.mSelectedTransactionsToInclude.clear()
            mViewModel.mSelectedTransactionsToInclude.add(transaction)
            mViewModel.validateTransaction()
        }
    }

    override fun onTransactionsLinkedSuccess(report: Report) {
        refreshPreviousScreen()
        mViewModel.mReport = report
        mTransactionsFragment.onTransactionsLinkedSuccess()
        mBinding.amountTv.text = mViewModel.mReport.displayAmount()
        mViewModel.mSelectedTransactionsToInclude.clear()
    }

    override fun onCreateReportCommentSuccess() {
        if (this::mCommentsFragment.isInitialized)
            mCommentsFragment.fetch()
    }

    override fun onDeleteReportSuccess() {
        refreshPreviousScreen()
        onBackPressed()
    }

    override fun onRecallReportSuccess() {
        refreshPreviousScreen()
        mViewModel.getReport()
    }

    override fun onReportSubmittedSuccess() {
        refreshPreviousScreen()
        onBackPressed()
    }

    override fun onTripLinkingSuccess(trips: List<Trip>) {
        refreshPreviousScreen()
        mTripsFragment.onTripLinkedSuccessfully()
    }

    override fun onAdvanceLinkingSuccess(advance: List<Advance>) {
        mAdvancesFragment.onAdvanceLinkedSuccessfully()
    }

    private fun buildReportTabs() {
        mBinding.reportVp.offscreenPageLimit = 6
        mBinding.reportVp.isUserInputEnabled = false
        mBinding.reportTabs.tabRippleColor = null
        val viewPagerAdapter = ViewPagerAdapter(this)
        mTransactionsFragment = TransactionsFragment.newInstance(
            TransactionsFragment.TAG_TRANSACTIONS,
            reportId = mViewModel.mId,
            isSearchAndFilterEnabled = false
        )
        mAdvancesFragment =
            TransactionsFragment.newInstance(
                TransactionsFragment.TAG_ADVANCES,
                reportId = mViewModel.mId,
                isSearchAndFilterEnabled = false
            )
        mTripsFragment =
            TransactionsFragment.newInstance(
                TransactionsFragment.TAG_TRIPS,
                reportId = mViewModel.mId,
                isSearchAndFilterEnabled = false
            )
        mCommentsFragment = CommentsFragment.newInstance(mViewModel.mId)
        viewPagerAdapter.addFrag(mTransactionsFragment, TransactionsFragment.TAG_EXPENSES)
        viewPagerAdapter.addFrag(mAdvancesFragment, TransactionsFragment.TAG_ADVANCES)
        viewPagerAdapter.addFrag(mTripsFragment, TransactionsFragment.TAG_TRIPS)
        viewPagerAdapter.addFrag(
            DetailsFragment.newInstance(mViewModel.mId),
            DetailsFragment.TAG
        )
//        viewPagerAdapter.addFrag(
//            ApprovalFlowFragment.newInstance(null, mReport, null, null),
//            ApprovalFlowFragment.TAG
//        )
        viewPagerAdapter.addFrag(
            HistoryFragment.newInstance(mViewModel.mId),
            HistoryFragment.TAG
        )
        viewPagerAdapter.addFrag(
            mCommentsFragment,
            CommentsFragment.TAG
        )
        mBinding.reportVp.adapter = viewPagerAdapter
        TabLayoutMediator(
            mBinding.reportTabs,
            mBinding.reportVp
        ) { tab, position ->
            tab.text = viewPagerAdapter.mFragmentTitleList[position]
        }.attach()
    }

    override fun onTransactionUnLined() {
        refreshPreviousScreen()
        mViewModel.getReport()
    }

    override fun canUnlink(): Boolean {
        return mViewModel.mReport.canEdit()
    }


    override fun onApplyFilter(list: List<Filters>) {
    }

    override fun continueWithWarnings(linkTransactionsToRequest: LinkTransactionsToRequest?) {
        mViewModel.submitOrLink()
    }

    override fun onReload() {

    }

    override fun getRecord(): Record {
        return mViewModel.mReport
    }

    override fun getType(): String {
        return "Report"
    }
}