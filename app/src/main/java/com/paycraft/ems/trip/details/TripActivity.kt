package com.paycraft.ems.trip.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.paycraft.R
import com.paycraft.base.ViewPagerAdapter
import com.paycraft.base.dialog
import com.paycraft.base.toast
import com.paycraft.databinding.ActivityTripBinding
import com.paycraft.ems.Record
import com.paycraft.ems.RecordStatus.Companion.STATUS_APPROVED
import com.paycraft.ems.RecordStatus.Companion.STATUS_CANCELLED
import com.paycraft.ems.RecordStatus.Companion.STATUS_CLOSED
import com.paycraft.ems.RecordStatus.Companion.STATUS_PENDING_APPROVAL
import com.paycraft.ems.RecordStatus.Companion.STATUS_PENDING_RECOVER
import com.paycraft.ems.RecordStatus.Companion.STATUS_UN_SUBMITTED
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
import com.paycraft.ems.trip.create.CreateTripActivity
import com.paycraft.ems.trip.create.Trip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripActivity : RecordActivity(), View.OnClickListener, TripView,
    ReportPickerBottomFragmentListener, OptionsListener, CreateCommentBottomSheetListener,
    RecordFragmentListener {
    var id: String? = ""

    companion object {
        const val E_TRIP_ID = "trip_id"
        fun start(activity: Activity, id: String) {
            Intent(activity, TripActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.putExtra(E_TRIP_ID, id)
                }.run {
                    activity.startActivityForResult(this, RC_REFRESH_PREVIOUS_SCREEN)
                }
        }
    }

    private lateinit var mBinding: ActivityTripBinding
    private lateinit var mViewModel: TripViewModel
    private lateinit var mCommentsFragment: CommentsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getStringExtra(E_TRIP_ID)
        if (id.isNullOrEmpty()) {
            toast("Unknown Trip!")
            finish()
            return
        }
        mViewModel = ViewModelProvider(this)[TripViewModel::class.java]
        mViewModel.setView(this)
        mViewModel.id = id

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_trip)
        configToolbar(
            mBinding.toolbar,
            mBinding.toolbarTitleTv, "Trip"
        )

        mBinding.submitTv.setOnClickListener(this)
        mBinding.applyToReportTv.setOnClickListener(this)
        mBinding.moreIv.setOnClickListener(this)
        mBinding.editTv.setOnClickListener(this)
        mBinding.recallTv.setOnClickListener(this)
        mBinding.changeReportTv.setOnClickListener(this)

        mViewModel.getTrip()
        mViewModel.mReport.observe(this) {
            ReportPickerBottomFragment.start(supportFragmentManager, it)
        }
        mViewModel.getTrip()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_CANCELED == resultCode) return
        when (requestCode) {
            CreateTripActivity.RC -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                mViewModel.getTrip()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.recallTv -> {
                mViewModel.recallTrip()
            }

            R.id.editTv -> {
                mViewModel.mTrip.id?.let { CreateTripActivity.start(this, it) }
            }

            R.id.moreIv -> {
                val options = arrayListOf(
                    FieldOption("add_comments", "Add Comments"),
                )
                if (mViewModel.mTrip.status != STATUS_CLOSED && mViewModel.mTrip.status != STATUS_CANCELLED) {
                    options.add(FieldOption("cancel_trip", "Cancel Trip"))
                }
                if (mViewModel.mTrip.status == STATUS_APPROVED) {
                    options.add(FieldOption("close_trip", "Mark as Close"))
                }
                if (mViewModel.mTrip.canDelete()) {
                    options.add(FieldOption("delete", "Delete"))
                }
                if (options.isEmpty()) return
                OptionsBottomSheetFragment.start(
                    supportFragmentManager,
                    title = "Trip Options",
                    options = options
                )
            }

            R.id.changeReportTv, R.id.applyToReportTv -> {
                mViewModel.reports(1)
            }

            R.id.submitTv -> {
                mViewModel.submitTrip()
            }
        }
    }

    override fun onTripSuccess(trip: Trip) {
        mViewModel.mTrip = trip
        mBinding.expenseIdValueTv.text = mViewModel.mTrip.number ?: ""
        mBinding.reportTitleTv.text = mViewModel.mTrip.title ?: "-"
        mBinding.startEndDateTv.text = mViewModel.mTrip.createdAt ?: "-"
        mBinding.reportStatusTv.text = mViewModel.mTrip.displayStatus()
        statusColors(
            this,
            trip.status ?: "",
            mBinding.expenseIndicatorView,
            mBinding.reportStatusTv
        )
        when (mViewModel.mTrip.status) {
            STATUS_PENDING_APPROVAL -> {
                mBinding.applyToReportTv.visibility = View.GONE
                mBinding.submitTv.visibility = View.GONE
                mBinding.editTv.visibility = View.GONE
                mBinding.recallTv.visibility =
                    if (trip.recallable == true) View.VISIBLE else View.GONE
            }

            STATUS_PENDING_RECOVER -> {
                mBinding.applyToReportTv.visibility = View.GONE
                mBinding.submitTv.visibility = View.GONE
                mBinding.editTv.visibility = View.GONE
                mBinding.recallTv.visibility = View.GONE
            }

            STATUS_UN_SUBMITTED -> {
                mBinding.applyToReportTv.visibility = View.GONE
                mBinding.submitTv.visibility = View.VISIBLE
                mBinding.editTv.visibility = View.VISIBLE
                mBinding.recallTv.visibility = View.GONE
            }

            STATUS_CLOSED,
            STATUS_APPROVED -> {
                if (!mViewModel.mTrip.report.isNullOrEmpty()) {
                    mBinding.applyToReportTv.visibility = View.GONE
                    if (STATUS_UN_SUBMITTED == (mViewModel.mTrip.report?.firstOrNull()?.status
                            ?: "")
                    )
                        mBinding.changeReportTv.visibility = View.VISIBLE
                    else
                        mBinding.changeReportTv.visibility = View.GONE
                } else {
                    mBinding.applyToReportTv.visibility = View.VISIBLE
                    mBinding.changeReportTv.visibility = View.GONE
                }
                mBinding.submitTv.visibility = View.GONE
                mBinding.editTv.visibility = View.GONE
                mBinding.recallTv.visibility = View.GONE
            }

            else -> {
                mBinding.applyToReportTv.visibility = View.GONE
                mBinding.submitTv.visibility = View.GONE
                mBinding.editTv.visibility = View.GONE
                mBinding.recallTv.visibility = View.GONE
            }
        }
        buildReportTabs()
    }

    override fun onTripSubmittedSuccess() {
        refreshPreviousScreen()
        mViewModel.getTrip()
    }

    override fun onTripLinkingSuccess() {
        refreshPreviousScreen()
        mViewModel.getTrip()
    }

    override fun onDeleteTripSuccess() {
        refreshPreviousScreen()
        mViewModel.getTrip()
    }

    override fun onCreateTripCommentSuccess() {
        if (this::mCommentsFragment.isInitialized)
            mCommentsFragment.fetch(1)
    }

    override fun onRecallTripSuccess() {
        refreshPreviousScreen()
        mViewModel.getTrip()
    }

    override fun cancelTripSuccess() {
        refreshPreviousScreen()
        onBackPressed()
    }

    override fun closeTripSuccess() {
        refreshPreviousScreen()
        onBackPressed()
    }

    override fun onReportSelected(report: Report) {
        mViewModel.mTrip.id?.let { aId ->
            report.id?.let { rId ->
                mViewModel.linkTripToReport(
                    LinkTripsToRequest(
                        rId,
                        arrayListOf(aId)
                    )
                )
            }
        }
    }

    override fun onOptionSelected(type: String, fieldOption: FieldOption) {
        when (fieldOption.id) {
            "cancel_trip" -> {
                dialog(
                    getString(R.string.cancel_trip),
                    getString(R.string.dialog_yes),
                    getString(R.string.dialog_no)
                ) { _, _ ->
                    mViewModel.cancelTrip()
                }
            }

            "close_trip" -> {
                dialog(
                    getString(R.string.close_trip),
                    getString(R.string.dialog_yes),
                    getString(R.string.dialog_no)
                ) { _, _ ->
                    mViewModel.closeTrip()
                }
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
                    mViewModel.deleteTrip()
                }
            }
        }
    }

    override fun onSubmitComment(comment: String) {
        mViewModel.createTripComment(comment)
    }

    private fun buildReportTabs() {
        mBinding.reportVp.offscreenPageLimit = 6
        mBinding.reportVp.isUserInputEnabled = false
        mBinding.reportTabs.tabRippleColor = null;
        val viewPagerAdapter = ViewPagerAdapter(this)
        mCommentsFragment = CommentsFragment.newInstance(mViewModel.id)
        viewPagerAdapter.addFrag(
            TripItineraryFragment.newInstance(mViewModel.mTrip),
            TripItineraryFragment.TAG
        )
        viewPagerAdapter.addFrag(
            DetailsFragment.newInstance(mViewModel.id),
            DetailsFragment.TAG
        )
        /*viewPagerAdapter.addFrag(
            ApprovalFlowFragment.newInstance(mViewModel.mTrip),
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

    override fun getRecord(): Record {
        return mViewModel.mTrip
    }

    override fun getType(): String {
        return "Trip"
    }
}