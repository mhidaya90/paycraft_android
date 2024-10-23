package com.paycraft.ems.transactions.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout.GONE
import com.google.android.material.tabs.TabLayout.INVISIBLE
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayout.VISIBLE
import com.google.android.material.tabs.TabLayoutMediator
import com.paycraft.R
import com.paycraft.base.SessionManager
import com.paycraft.base.ViewPagerAdapter
import com.paycraft.base.dialog
import com.paycraft.base.toast
import com.paycraft.card.dispute.CreateDisputeBottomSheetFragment
import com.paycraft.databinding.ActivityTransactionBinding
import com.paycraft.ems.PolicyViolationsBottomSheetFragmentListener
import com.paycraft.ems.Record
import com.paycraft.ems.RecordStatus.Companion.STATUS_PENDING
import com.paycraft.ems.RecordStatus.Companion.STATUS_REPORTED
import com.paycraft.ems.attachments.AttachmentsFragment
import com.paycraft.ems.comments.CommentsFragment
import com.paycraft.ems.comments.CreateCommentBottomSheetFragment
import com.paycraft.ems.comments.CreateCommentBottomSheetListener
import com.paycraft.ems.history.HistoryFragment
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.options_picker.OptionsListener
import com.paycraft.ems.report_picker.ReportPickerBottomFragment
import com.paycraft.ems.report_picker.ReportPickerBottomFragmentListener
import com.paycraft.ems.reports.PolicyViolationsBottomSheetFragment
import com.paycraft.ems.reports.Report
import com.paycraft.ems.reports.detail.DisplayViolation
import com.paycraft.ems.transaction_picker.LinkTransactionsToRequest
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.RecordActivity
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.transactions.create.CreateTransactionActivity
import com.paycraft.ems.transactions.create.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionActivity : RecordActivity(), TransactionView, OptionsListener,
    CreateCommentBottomSheetListener, ReportPickerBottomFragmentListener,
    PolicyViolationsBottomSheetFragmentListener, View.OnClickListener, RecordFragmentListener {

    companion object {
        const val E_TRANSACTION_ID = "id"
        fun start(activity: Activity, id: String) {
            Intent(activity, TransactionActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.putExtra(E_TRANSACTION_ID, id)
                }.run {
                    activity.startActivityForResult(this, 100)
                }
        }
    }

    private lateinit var mViewModel: TransactionViewModel
    private lateinit var mBinding: ActivityTransactionBinding
    lateinit var mCommentsFragment: CommentsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getStringExtra(E_TRANSACTION_ID)
        if (id.isNullOrEmpty()) {
            toast("Unknown Transaction!")
            onBackPressed()
            return
        }
        mViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        mViewModel.setView(this)
        mViewModel.id = id
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transaction)
        configToolbar(
            mBinding.toolbar,
            mBinding.toolbarTitleTv, "Transaction"
        )

        mBinding.moreIv.setOnClickListener(this)
        mBinding.editTv.setOnClickListener(this)
        mBinding.addToReportTv.setOnClickListener(this)
        mBinding.changeReportTv.setOnClickListener(this)
        mBinding.viewViolationsTv.setOnClickListener(this)
        mBinding.raiseDisputeTv.setOnClickListener(this)

        mViewModel.mViolations.observe(this) { v ->
            PolicyViolationsBottomSheetFragment.start(
                supportFragmentManager,
                v as ArrayList<DisplayViolation>
            )
        }
        mViewModel.getTransaction()
    }

    override fun onTransactionSuccess(transaction: Transaction) {
        mViewModel.mTransaction = transaction
        mBinding.expenseIdValueTv.text = mViewModel.mTransaction.number ?: ""
        mBinding.transactionTitleTv.text = mViewModel.mTransaction.merchant ?: ""
        mBinding.startEndDateTv.text = mViewModel.mTransaction.dateAndTime ?: ""
        mBinding.amountTv.text = mViewModel.mTransaction.displayAmount()
        mBinding.transactionStatusTv.text = mViewModel.mTransaction.displayStatus()
        statusColors(
            this,
            transaction.status ?: "",
            mBinding.expenseIndicatorView,
            mBinding.transactionStatusTv
        )
        buildReportTabs()

        if (SessionManager.instance().hasExpenseManagement()) {
            mBinding.raiseDisputeTv.visibility = GONE
            when (transaction.status) {
                STATUS_PENDING -> {
                    mBinding.editTv.visibility = VISIBLE
                    mBinding.addToReportTv.visibility = VISIBLE
                    mBinding.changeReportTv.visibility = GONE
                }

                STATUS_REPORTED -> {
                    mBinding.editTv.visibility = INVISIBLE
                    mBinding.addToReportTv.visibility = GONE
                    mBinding.changeReportTv.visibility = VISIBLE
                }

                else -> {
                    mBinding.editTv.visibility = GONE
                    mBinding.addToReportTv.visibility = GONE
                    mBinding.changeReportTv.visibility = GONE
                }
            }
            mBinding.policyViolationsGp.visibility =
                if (transaction.expenseErrors?.getViolationList().isNullOrEmpty())
                    GONE
                else
                    VISIBLE

            mBinding.reportFooterIc.visibility =
                if (mViewModel.mTransaction.isCreditTransaction()) GONE else VISIBLE
        } else {
            mBinding.moreIv.visibility = GONE

            mBinding.editTv.visibility = GONE
            mBinding.addToReportTv.visibility = GONE
            mBinding.changeReportTv.visibility = GONE
            mBinding.raiseDisputeTv.visibility = VISIBLE
            mBinding.policyViolationsGp.visibility = GONE

            mBinding.reportFooterIc.visibility = VISIBLE
        }
    }

    override fun onUnSubmittedReportsSuccess(reports: List<Report>) {
        ReportPickerBottomFragment.start(supportFragmentManager, reports.filter {
            mViewModel.mTransaction.id != it.id
        })
    }

    override fun onCreateTransactionCommentSuccess() {
        if (this::mCommentsFragment.isInitialized)
            mCommentsFragment.fetch()
    }

    override fun onDeleteTransactionSuccess() {
        refreshPreviousScreen()
        onBackPressed()
    }

    private fun buildReportTabs() {
        mBinding.reportVp.offscreenPageLimit = 6
        mBinding.reportVp.isUserInputEnabled = false
        mBinding.reportTabs.tabRippleColor = null;
        val viewPagerAdapter = ViewPagerAdapter(this)
        mCommentsFragment = CommentsFragment.newInstance(mViewModel.id)
        viewPagerAdapter.addFrag(
            DetailsFragment.newInstance(mViewModel.id),
            DetailsFragment.TAG
        )
        viewPagerAdapter.addFrag(
            HistoryFragment.newInstance(mViewModel.id),
            HistoryFragment.TAG
        )
        viewPagerAdapter.addFrag(
            mCommentsFragment,
            CommentsFragment.TAG
        )
        viewPagerAdapter.addFrag(
            AttachmentsFragment.newInstance(),
            AttachmentsFragment.TAG
        )

        mBinding.reportVp.adapter = viewPagerAdapter
        TabLayoutMediator(
            mBinding.reportTabs,
            mBinding.reportVp
        ) { tab, position ->
            tab.text = viewPagerAdapter.mFragmentTitleList[position]
        }.attach()

        mBinding.reportVp.isUserInputEnabled = false
        mBinding.reportVp.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

        mBinding.reportTabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                if (tab.text.toString() == AttachmentsFragment.TAG)
                    mBinding.appbarCardContainer.setExpanded(false)
            }

            override fun onTabUnselected(tab: Tab) {}
            override fun onTabReselected(tab: Tab) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_CANCELED == resultCode) return
        when (requestCode) {
            CreateTransactionActivity.RC -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                mViewModel.getTransaction()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.raiseDisputeTv -> {
                CreateDisputeBottomSheetFragment.start(supportFragmentManager)
            }

            R.id.viewViolationsTv -> {
                PolicyViolationsBottomSheetFragment.start(
                    supportFragmentManager,
                    mViewModel.mTransaction.expenseErrors?.getViolationList() as ArrayList<DisplayViolation>
                )
            }

            R.id.editTv -> {
                CreateTransactionActivity.start(
                    this,
                    mViewModel.id,
                    mViewModel.mTransaction.isCardTransaction()
                )
            }

            R.id.changeReportTv, R.id.addToReportTv -> {
                mViewModel.reports()
            }

            R.id.moreIv -> {
                val options = arrayListOf(
                    FieldOption("add_comments", "Add Comments"),
                )
                if (mViewModel.mTransaction.canDelete() && !mViewModel.mTransaction.isCardTransaction()) {
                    options.add(FieldOption("delete", "Delete"))
                }
                if (options.isEmpty()) return
                OptionsBottomSheetFragment.start(supportFragmentManager, options = options)
            }
        }
    }

    override fun onOptionSelected(type: String, fieldOption: FieldOption) {
        when (fieldOption.id) {
            "add_comments" -> {
                CreateCommentBottomSheetFragment.start(supportFragmentManager)
            }

            "delete" -> {
                dialog(
                    getString(R.string.delete_confirmation),
                    getString(R.string.dialog_yes),
                    getString(R.string.dialog_no)
                ) { _, _ ->
                    mViewModel.deleteTransaction()
                }
            }
        }
    }

    override fun onSubmitComment(comment: String) {
        mViewModel.createTransactionComment(comment)
    }

    override fun onCreateReportCommentSuccess() {
        mCommentsFragment.fetch()
    }

    override fun onTransactionLinedToReport() {
        mViewModel.getTransaction()
    }

    override fun onReportSelected(report: Report) {
        report.id?.let { rId ->
            mViewModel.mTransaction.id?.let { tId ->
                mViewModel.validateTransaction(
                    LinkTransactionsToRequest(
                        rId,
                        arrayListOf(tId)
                    )
                )
            }
        }
    }

    override fun continueWithWarnings(linkTransactionsToRequest: LinkTransactionsToRequest?) {
        linkTransactionsToRequest?.let { mViewModel.linkTransactionToReport(it) }
    }

    override fun getRecord(): Record {
        return mViewModel.mTransaction
    }

    override fun getType(): String {
        return "Transaction"
    }
}