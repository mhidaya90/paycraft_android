package com.paycraft.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.paycraft.BuildConfig
import com.paycraft.base.MyFirebaseMessagingService.Companion.E_ID
import com.paycraft.base.MyFirebaseMessagingService.Companion.E_PAGE
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.SessionManager
import com.paycraft.base.toast
import com.paycraft.card.HomeCardsAdapterListener
import com.paycraft.card.add.otp.AddCardActivity
import com.paycraft.card.add.otp.CARD_ACTIVATION
import com.paycraft.card.add.verify.CardVerificationActivity
import com.paycraft.card.cards.Card
import com.paycraft.card.cards.CardsActivity
import com.paycraft.card.cards.HomeCardsAdapter
import com.paycraft.card.preferences.PreferencesActivity
import com.paycraft.databinding.ActivityHomeBinding
import com.paycraft.ems.ExpenseManagementActivity
import com.paycraft.ems.RecordsActivity
import com.paycraft.ems.TransactionsFragment
import com.paycraft.ems.advance.create.CreateAdvanceActivity
import com.paycraft.ems.advance.detail.AdvanceActivity
import com.paycraft.ems.reports.Report
import com.paycraft.ems.reports.create.CreateReportActivity
import com.paycraft.ems.reports.detail.ReportActivity
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.transactions.TransactionFile
import com.paycraft.ems.transactions.TransactionsAdapter
import com.paycraft.ems.transactions.TransactionsAdapterListener
import com.paycraft.ems.transactions.create.CreateTransactionActivity
import com.paycraft.ems.transactions.detail.TransactionActivity
import com.paycraft.ems.trip.create.CreateTripActivity
import com.paycraft.ems.trip.details.TripActivity
import com.paycraft.file_picker.FilePickerBottomSheetFragment
import com.paycraft.file_picker.FilePickerBottomSheetFragmentListener
import com.paycraft.notifications.NotificationsActivity
import com.paycraft.user.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class HomeActivity : BaseActivity(), HomeView, TransactionsAdapterListener,
    FilePickerBottomSheetFragmentListener, HomeCardsAdapterListener,
    View.OnClickListener {
    companion object {
        fun intent(context: Context, page: String? = null, id: String? = null): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK
            page?.let {
                intent.putExtra(E_PAGE, it)
            }
            id?.let {
                intent.putExtra(E_ID, it)
            }
            return intent
        }

        fun start(context: Context, page: String? = null, id: String? = null) {
            context.startActivity(intent(context, page, id))
        }
    }

    private lateinit var mBinding: ActivityHomeBinding
    val mViewModel by viewModels<HomeViewModel>()
    private lateinit var mTransactionsAdapter: TransactionsAdapter
    private lateinit var mReportsAdapter: TransactionsAdapter
    private lateinit var mCardsAdapter: HomeCardsAdapter
    private var isReportSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        mBinding.layoutHomeOptions.versionTv.text =
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        mBinding.layoutHomeMain.viewAllExpensesTv.setOnClickListener(this)
        mBinding.layoutHomeMain.viewAllReportsTv.setOnClickListener(this)
        mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.viewAllTv.setOnClickListener(this)
        mBinding.layoutHomeOptions.expensesTv.setOnClickListener(this)
        mBinding.layoutHomeOptions.expenseReportsTv.setOnClickListener(this)
        mBinding.layoutHomeOptions.advancesTv.setOnClickListener(this)
        mBinding.layoutHomeOptions.tripsTv.setOnClickListener(this)
        mBinding.layoutHomeOptions.personalDetailsTv.setOnClickListener(this)
        mBinding.layoutHomeOptions.helpSupportTv.setOnClickListener(this)
        mBinding.layoutHomeOptions.cardsTv.setOnClickListener(this)
        mBinding.layoutHomeMain.layoutHomeSmallAddCard.viewCardMiniTv.setOnClickListener(this)
        mBinding.layoutHomeBigAddCard.addCardTv.setOnClickListener(this)
        mBinding.layoutHomeMain.layoutHomeSmallAddCard.addCardMiniTv.setOnClickListener(this)
        mBinding.layoutHomeMain.viewAllCardsTv.setOnClickListener(this)
        mBinding.layoutHomeOptions.preferenceTv.setOnClickListener(this)
        mBinding.layoutHomeOptions.privacyPolicyTv.setOnClickListener(this)

        mViewModel.setView(this)

        mViewModel.id = intent.getStringExtra(E_ID)
        mViewModel.page = intent.getStringExtra(E_PAGE)

        mTransactionsAdapter =
            TransactionsAdapter(this, R.layout.row_expense, this, emptyList())
        mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.transactionsRv.adapter =
            mTransactionsAdapter
        mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.transactionsRv.layoutManager =
            LinearLayoutManager(this, VERTICAL, false)

        mReportsAdapter = TransactionsAdapter(this, R.layout.row_report, this, emptyList())
        mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportsRv.adapter = mReportsAdapter
        mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportsRv.layoutManager =
            LinearLayoutManager(this, VERTICAL, false)

        mCardsAdapter = HomeCardsAdapter(this, emptyList())
        mBinding.layoutHomeMain.cardsRv.adapter = mCardsAdapter
        mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportsRv.layoutManager =
            LinearLayoutManager(this, VERTICAL, false)

        subscriptions()
        notificationNavigation()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.profile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_CANCELED == resultCode) return
        when (requestCode) {
            RC_REFRESH_PREVIOUS_SCREEN -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                if (!refreshPreviousScreen) return
                if (isReportSelected) {
                    mViewModel.reports()
                } else {
                    mViewModel.transactions()
                }
            }

            CreateTransactionActivity.RC -> {
                mViewModel.transactions()
            }

            CreateReportActivity.RC -> {
                mViewModel.reports()
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.preferenceTv -> {
                PreferencesActivity.start(this)
            }

            R.id.viewAllCardsTv, R.id.viewCardMiniTv, R.id.cardsTv -> {
                CardsActivity.start(this)
            }

            R.id.personalDetailsTv -> {
                ProfileActivity.start(this)
            }

            R.id.helpSupportTv -> {
                HelpActivity.start(this)
            }

            R.id.tripsTv -> {
                if (SessionManager.instance().hasExpenseManagement()) RecordsActivity.start(
                    this,
                    TransactionsFragment.TAG_TRIPS
                )
            }

            R.id.advancesTv -> {
                if (SessionManager.instance().hasExpenseManagement()) RecordsActivity.start(
                    this,
                    TransactionsFragment.TAG_ADVANCES
                )
            }

            R.id.expensesTv, R.id.viewAllExpensesTv -> {
                if (SessionManager.instance()
                        .hasExpenseManagement()
                ) ExpenseManagementActivity.start(this)
            }

            R.id.expenseReportsTv, R.id.viewAllReportsTv -> {
                if (SessionManager.instance()
                        .hasExpenseManagement()
                ) ExpenseManagementActivity.start(this, true)
            }

            R.id.viewAllTv -> {
                ExpenseManagementActivity.start(this, isReportSelected)
            }

            R.id.expenseHomeTabTv -> {
                isReportSelected = false
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.transactionsRv.visibility =
                    VISIBLE
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportsRv.visibility = GONE
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.expenseHomeTabTv.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black33
                    )
                )
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportHomeTabTv.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black45
                    )
                )
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.expenseHomeTabIndicatorTv.setBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.black33
                    )
                )
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportHomeTabIndicatorTv.setBackgroundColor(
                    ContextCompat.getColor(
                        this, android.R.color.transparent
                    )
                )
                mViewModel.transactions()
            }

            R.id.reportHomeTabTv -> {
                isReportSelected = true
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.transactionsRv.visibility = GONE
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportsRv.visibility = VISIBLE
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.expenseHomeTabTv.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black45
                    )
                )
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportHomeTabTv.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black33
                    )
                )
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.expenseHomeTabIndicatorTv.setBackgroundColor(
                    ContextCompat.getColor(
                        this, android.R.color.transparent
                    )
                )
                mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportHomeTabIndicatorTv.setBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.black33
                    )
                )
                mViewModel.reports()
            }

            R.id.notificationsIv -> {
                NotificationsActivity.start(this)
            }

            R.id.addCardMiniTv, R.id.addCardTv -> {
                AddCardActivity.start(this)
            }

            R.id.homeIv -> {
                mViewModel.isMenuOpened = false
                homeUi()
            }

            R.id.profileIv -> {
                mBinding.homeCl.visibility = GONE
                mBinding.addCardCv.visibility = GONE
                mBinding.profileLl.visibility = VISIBLE
                mViewModel.isMenuOpened = true
            }

            R.id.addIv -> {
                if (!SessionManager.instance().hasExpenseManagement()) {
                    return
                }
                val dialog = customDialog(R.layout.dialog_app_options)
                dialog.findViewById<View>(R.id.addTripsIv).setOnClickListener {
                    dialog.dismiss()
                    CreateTripActivity.start(this)
                }
                dialog.findViewById<View>(R.id.addAdvanceIv).setOnClickListener {
                    dialog.dismiss()
                    CreateAdvanceActivity.start(this)
                }
                dialog.findViewById<View>(R.id.scanReportIv).setOnClickListener {
                    dialog.dismiss()
                    FilePickerBottomSheetFragment.start(supportFragmentManager)
                }
                dialog.findViewById<View>(R.id.addExpenseManuallyIv).setOnClickListener {
                    dialog.dismiss()
                    CreateTransactionActivity.start(this)
                }

                dialog.findViewById<View>(R.id.addExpenseReportIv).setOnClickListener {
                    dialog.dismiss()
                    CreateReportActivity.start(this)
                }
                dialog.findViewById<View>(R.id.closeAppOptionsDialogIv).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }

            R.id.privacyPolicyTv -> {
                PoliciesActivity.start(this)
            }

            R.id.signoutTv -> {
                mViewModel.logout()
            }
        }
    }

    override fun onOtpSent(id: String, trxId: String) {
        CardVerificationActivity.start(this, true, id, trxId)
        finish()
    }

    override fun onTransactionClicked(transaction: Transaction) {
        transaction.id?.let { TransactionActivity.start(this, it) }
    }

    override fun isSelected(transaction: Transaction): Boolean {
        return false
    }

    override fun onReportClicked(report: Report) {
        report.id?.let { ReportActivity.start(this, it) }
    }

    override fun onFileSelected(file: TransactionFile) {
        toast(file.toString())
    }

    override fun onClickHomeCard(card: Card, cardIndex: Int) {
        CardsActivity.start(this, cardIndex)
    }

    override fun onClickActivateCard(card: Card) {
        card.cardReferenceNo?.let { mViewModel.addCardSendOtpAsync(it, CARD_ACTIVATION) }
    }

    override fun addNewCard(card: Card) {
        AddCardActivity.start(this)
    }

    private fun homeUi() {
        if (SessionManager.instance().hasExpenseManagement()) {
            mBinding.companyNameTv.visibility = VISIBLE

            mBinding.layoutHomeMain.unreportedExpensesCv.visibility = VISIBLE
            mBinding.layoutHomeMain.pendingActionsTextTv.visibility = VISIBLE
            mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportHomeTabTv.visibility = VISIBLE
            mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.viewAllTv.visibility = VISIBLE
            mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.expenseHomeTabTv.text =
                getString(R.string.expenses)

            mBinding.layoutHomeOptions.expensesTv.visibility = VISIBLE
            mBinding.layoutHomeOptions.expensesView.visibility = VISIBLE
            mBinding.layoutHomeOptions.expenseReportsTv.visibility = VISIBLE
            mBinding.layoutHomeOptions.expenseReportsView.visibility = VISIBLE
            mBinding.layoutHomeOptions.advancesTv.visibility = VISIBLE
            mBinding.layoutHomeOptions.advancesView.visibility = VISIBLE
            mBinding.layoutHomeOptions.tripsTv.visibility = VISIBLE
            mBinding.layoutHomeOptions.tripsView.visibility = VISIBLE
        } else {
            mBinding.companyNameTv.visibility = GONE

            mBinding.layoutHomeMain.unreportedExpensesCv.visibility = GONE
            mBinding.layoutHomeMain.pendingActionsTextTv.visibility = GONE
            mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.reportHomeTabTv.visibility = GONE
            mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.viewAllTv.visibility = GONE
            mBinding.layoutHomeMain.layoutHomeExpenseReportTabs.expenseHomeTabTv.text =
                getString(R.string.recent_transaction)

            mBinding.layoutHomeOptions.expensesTv.visibility = GONE
            mBinding.layoutHomeOptions.expensesView.visibility = GONE
            mBinding.layoutHomeOptions.expenseReportsTv.visibility = GONE
            mBinding.layoutHomeOptions.expenseReportsView.visibility = GONE
            mBinding.layoutHomeOptions.advancesTv.visibility = GONE
            mBinding.layoutHomeOptions.advancesView.visibility = GONE
            mBinding.layoutHomeOptions.tripsTv.visibility = GONE
            mBinding.layoutHomeOptions.tripsView.visibility = GONE
        }

        if (mViewModel.mUser.value?.cards?.isEmpty() == false) {
            mBinding.homeCl.visibility = VISIBLE
        } else {
            mBinding.homeCl.visibility = GONE
        }

        if (mViewModel.mUser.value?.isAgent() == true) {
            if (mViewModel.mUser.value?.noCards() == true) {
                mBinding.homeCl.visibility = GONE
                mBinding.profileLl.visibility = GONE
                mBinding.addCardCv.visibility = VISIBLE
                mBinding.addIv.visibility = INVISIBLE
            } else {
                mBinding.homeCl.visibility = VISIBLE
                mBinding.layoutHomeMain.addCardMiniCv.visibility = GONE
                mBinding.layoutHomeMain.cardsCv.visibility = VISIBLE
                mBinding.layoutHomeMain.expenseManagementCl.visibility = GONE
                mBinding.addIv.visibility = INVISIBLE
            }
        } else {
            if ((!SessionManager.instance()
                    .hasExpenseManagement()) && (mViewModel.mUser.value?.noCards() == true)
            ) {
                mBinding.layoutHomeMain.expenseManagementCl.visibility = GONE
                mBinding.homeCl.visibility = GONE
                mBinding.addCardCv.visibility = VISIBLE
            } else {
                mBinding.homeCl.visibility = VISIBLE
                mBinding.profileLl.visibility = GONE
                mBinding.addCardCv.visibility = GONE
            }
            if (mViewModel.isMenuOpened) {
                mBinding.profileIv.performClick()
            }
        }
        userAccess(if (mViewModel.mUser.value?.isAgent()==true) GONE else VISIBLE)
    }

    private fun userAccess(visibility: Int) {
        mBinding.layoutHomeOptions.expensesTv.visibility = visibility
        mBinding.layoutHomeOptions.expensesView.visibility = visibility
        mBinding.layoutHomeOptions.expenseReportsTv.visibility = visibility
        mBinding.layoutHomeOptions.expenseReportsView.visibility = visibility
        mBinding.layoutHomeOptions.advancesTv.visibility = visibility
        mBinding.layoutHomeOptions.advancesView.visibility = visibility
        mBinding.layoutHomeOptions.tripsTv.visibility = visibility
        mBinding.layoutHomeOptions.tripsView.visibility = visibility
        mBinding.layoutHomeOptions.privacyPolicyTv.visibility = visibility
        mBinding.layoutHomeOptions.privacyPolicyView.visibility = visibility
    }

    private fun notificationNavigation() {
        mViewModel.id?.let { id ->
            when (mViewModel.page) {
                "report" -> {
                    ReportActivity.start(this, id)
                }

                "expense", "transaction" -> {
                    TransactionActivity.start(this, id)
                }

                "trip" -> {
                    TripActivity.start(this, id)
                }

                "advance" -> {
                    AdvanceActivity.start(this, id)
                }

                else -> {}
            }
        }
    }

    private fun subscriptions() {
        mViewModel.mError.observe(this) {

        }
        mViewModel.mUser.observe(this) { u ->
            //notification count
            mBinding.notificationCountTv.text =
                if ((u.unreadNotificationCount
                        ?: 0) > 99
                ) "99+" else u.unreadNotificationCount.toString()
            mBinding.userNameTv.text = SessionManager.instance().getName()
            SessionManager.instance().setHasExpenseManagement(u.has_expense_management ?: false)

            //expense count
            mBinding.layoutHomeMain.expenseCountTv.text = getString(
                R.string.un_submitted_count_expenses, u.unreported_expense_count ?: 0
            )

            //report count
            mBinding.layoutHomeMain.reportCountTv.text = getString(
                R.string.un_submitted_count_reports, u.unsubmitted_reports_count ?: 0
            )
            //cards
            u.cards?.let { c ->
                if (c.isEmpty()) {
                    mBinding.layoutHomeMain.addCardMiniCv.visibility = VISIBLE
                    mBinding.layoutHomeMain.cardsCv.visibility = GONE
                } else {
                    mBinding.layoutHomeMain.addCardMiniCv.visibility = GONE
                    mBinding.layoutHomeMain.cardsCv.visibility = VISIBLE
                    //Adding empty card (Duplicating the last card)
                    val cards = c as ArrayList<Card>
                    cards.lastOrNull()?.let { card ->
                        cards.add(card)
                    }
                    mCardsAdapter.notifyWithData(cards)
                }
            }
            u.transactions?.let { t ->
                mTransactionsAdapter.notifyWithData(t)
            }
            //Transactions
            homeUi()
        }
        mViewModel.mTransactions.observe(this) {
            mTransactionsAdapter.notifyWithData(it)
        }
        mViewModel.mReports.observe(this) {
            mReportsAdapter.notifyWithData(it)
        }

    }
}