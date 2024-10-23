package com.paycraft.ems.advance.detail

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
import com.paycraft.databinding.ActivityAdvanceBinding
import com.paycraft.ems.Record
import com.paycraft.ems.RecordStatus.Companion.STATUS_APPROVED
import com.paycraft.ems.RecordStatus.Companion.STATUS_PENDING_APPROVAL
import com.paycraft.ems.RecordStatus.Companion.STATUS_PENDING_RECOVER
import com.paycraft.ems.RecordStatus.Companion.STATUS_UN_SUBMITTED
import com.paycraft.ems.advance.create.CreateAdvanceActivity
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.comments.CommentsFragment
import com.paycraft.ems.comments.CreateCommentBottomSheetFragment
import com.paycraft.ems.comments.CreateCommentBottomSheetListener
import com.paycraft.ems.history.HistoryFragment
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.options_picker.OptionsListener
import com.paycraft.ems.report_picker.ReportPickerBottomFragment
import com.paycraft.ems.report_picker.ReportPickerBottomFragmentListener
import com.paycraft.ems.reports.Report
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.RecordActivity
import com.paycraft.ems.transactions.detail.DetailsFragment
import com.paycraft.ems.transactions.detail.RecordFragmentListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdvanceActivity : RecordActivity(), View.OnClickListener, AdvanceView,
    ReportPickerBottomFragmentListener,
    CreateCommentBottomSheetListener, OptionsListener, RecordFragmentListener {
    companion object {
        const val E_ADVANCE_ID = "advance_id"
        fun start(activity: Activity, id: String) {
            Intent(activity, AdvanceActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.putExtra(E_ADVANCE_ID, id)
                }.run {
                    activity.startActivityForResult(this, RC_REFRESH_PREVIOUS_SCREEN)
                }
        }
    }

    private lateinit var mBinding: ActivityAdvanceBinding
    private lateinit var mViewModel: AdvanceViewModel
    private lateinit var mCommentsFragment: CommentsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getStringExtra(E_ADVANCE_ID)
        if (id.isNullOrEmpty()) {
            toast("Unknown Advance!")
            finish()
            return
        }
        mViewModel = ViewModelProvider(this)[AdvanceViewModel::class.java]
        mViewModel.setView(this)
        mViewModel.id = id

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_advance)
        configToolbar(
            mBinding.toolbar,
            mBinding.toolbarTitleTv, "Advance"
        )
        mBinding.submitTv.setOnClickListener(this)
        mBinding.applyToReportTv.setOnClickListener(this)
        mBinding.moreIv.setOnClickListener(this)
        mBinding.editTv.setOnClickListener(this)
        mBinding.recallTv.setOnClickListener(this)
        mViewModel.getAdvance()
        mViewModel.mReport.observe(this) {
            ReportPickerBottomFragment.start(supportFragmentManager, it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_CANCELED == resultCode) return
        when (requestCode) {
            CreateAdvanceActivity.RC -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                mViewModel.getAdvance()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.recallTv -> {
                mViewModel.recallAdvance()
            }

            R.id.editTv -> {
                mViewModel.mAdvance.id?.let { CreateAdvanceActivity.start(this, it) }
            }

            R.id.moreIv -> {
                val options = arrayListOf(
                    FieldOption("add_comments", "Add Comments"),
                )
                if (mViewModel.mAdvance.canDelete()) {
                    options.add(FieldOption("delete", "Delete"))
                }
                if (options.isEmpty()) return
                OptionsBottomSheetFragment.start(supportFragmentManager, options = options)
            }

            R.id.applyToReportTv -> {
                mViewModel.reports(1)
            }

            R.id.submitTv -> {
                mViewModel.submitAdvance()
            }
        }
    }

    override fun onAdvanceSuccess(advance: Advance) {
        mViewModel.mAdvance = advance
        mBinding.expenseIdValueTv.text = mViewModel.mAdvance.number ?: ""
        mBinding.reportTitleTv.text = mViewModel.mAdvance.title ?: "-"
        mBinding.startEndDateTv.text = mViewModel.mAdvance.advanceDate ?: "-"
        mBinding.reportStatusTv.text = mViewModel.mAdvance.displayStatus()
        mBinding.amountTv.text = mViewModel.mAdvance.displayAmount()
        statusColors(
            this,
            advance.status ?: "",
            mBinding.expenseIndicatorView,
            mBinding.reportStatusTv
        )
        when (advance.status) {
            STATUS_PENDING_APPROVAL -> {
                mBinding.applyToReportTv.visibility = GONE
                mBinding.submitTv.visibility = GONE
                mBinding.editTv.visibility = GONE
                mBinding.recallTv.visibility = if (advance.recallable == true) VISIBLE else GONE
            }

            STATUS_PENDING_RECOVER -> {
                mBinding.applyToReportTv.visibility = GONE
                mBinding.submitTv.visibility = GONE
                mBinding.editTv.visibility = GONE
                mBinding.recallTv.visibility = GONE
            }

            STATUS_UN_SUBMITTED -> {
                mBinding.applyToReportTv.visibility = GONE
                mBinding.submitTv.visibility = VISIBLE
                mBinding.editTv.visibility = VISIBLE
                mBinding.recallTv.visibility = GONE
            }

            STATUS_APPROVED -> {
                mBinding.applyToReportTv.visibility = VISIBLE
                mBinding.submitTv.visibility = GONE
                mBinding.editTv.visibility = GONE
                mBinding.recallTv.visibility = GONE
            }

            else -> {
                mBinding.applyToReportTv.visibility = GONE
                mBinding.submitTv.visibility = GONE
                mBinding.editTv.visibility = GONE
                mBinding.recallTv.visibility = GONE
            }
        }
        buildReportTabs()
    }

    override fun onAdvanceSubmittedSuccess() {
        refreshPreviousScreen()
        mViewModel.getAdvance()
    }

    override fun onAdvanceLinkingSuccess() {
        refreshPreviousScreen()
        mViewModel.getAdvance()
    }

    override fun onDeleteAdvanceSuccess() {
        refreshPreviousScreen()
        mViewModel.getAdvance()
    }

    override fun onCreateAdvanceCommentSuccess() {
        if (this::mCommentsFragment.isInitialized)
            mCommentsFragment.fetch(1)
    }

    override fun onRecallAdvanceSuccess() {
        refreshPreviousScreen()
        mViewModel.getAdvance()
    }

    override fun onReportSelected(report: Report) {
        mViewModel.mAdvance.id?.let { aId ->
            report.id?.let { rId ->
                mViewModel.linkAdvanceToReport(
                    LinkAdvancesToRequest(
                        rId,
                        arrayListOf(aId)
                    )
                )
            }
        }
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
        /* viewPagerAdapter.addFrag(
             ApprovalFlowFragment.newInstance(mViewModel.mAdvance),
             ApprovalFlowFragment.TAG
         )*/
        viewPagerAdapter.addFrag(
            HistoryFragment.newInstance(mViewModel.id),
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

    override fun onSubmitComment(comment: String) {
        mViewModel.createAdvanceComment(comment)
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
                    mViewModel.deleteAdvance()
                }
            }
        }
    }

    override fun getRecord(): Record {
        return mViewModel.mAdvance
    }

    override fun getType(): String {
        return "Advance"
    }
}