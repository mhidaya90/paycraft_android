package com.paycraft.ems

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.ViewPagerAdapter
import com.paycraft.databinding.ActivityExpenseManagementBinding
import com.paycraft.ems.filter.FilterPickerBottomSheetListener
import com.paycraft.ems.reports.create.CreateReportActivity
import com.paycraft.ems.transactions.Filters
import com.paycraft.ems.transactions.create.CreateTransactionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpenseManagementActivity : BaseActivity(), ExpenseManagementView,
    FilterPickerBottomSheetListener, TransactionsFragmentListener {
    companion object {
        val E_OPEN_REPORTS = "open_reports"
        fun start(context: Context, openReports: Boolean = false) {
            Intent(context, ExpenseManagementActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.putExtra(E_OPEN_REPORTS, openReports)
                }.run {
                    context.startActivity(this)
                }
        }
    }

    private lateinit var mBinding: ActivityExpenseManagementBinding
    lateinit var mTransactionFragment: TransactionsFragment
    lateinit var mReportsFragment: TransactionsFragment
    lateinit var mViewModel: ExpenseManagementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_expense_management)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, getString(R.string.title_manage_expenses)
        )
        mViewModel = ViewModelProvider(this)[ExpenseManagementViewModel::class.java]
        mViewModel.setView(this)
        mViewModel.isReportSelected = intent.getBooleanExtra(E_OPEN_REPORTS, false)
        buildReportTabs()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ems, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addRecord -> {
                if (mViewModel.isReportSelected) {
                    CreateReportActivity.start(this)
                } else {
                    CreateTransactionActivity.start(this)
                }
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_CANCELED == resultCode) return
        when (requestCode) {
            RC_REFRESH_PREVIOUS_SCREEN -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                if (!refreshPreviousScreen) return
                if (mViewModel.isReportSelected) {
                    mReportsFragment.refresh()
                } else {
                    mTransactionFragment.refresh()
                }
            }

            CreateTransactionActivity.RC -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                mTransactionFragment.refresh()
            }

            CreateReportActivity.RC -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                mReportsFragment.refresh()
            }
        }
    }

    override fun onApplyFilter(list: List<Filters>) {
        if (mBinding.reportVp.currentItem == 0) {
            mTransactionFragment.onApplyFilter(list)
        } else if (mBinding.reportVp.currentItem == 1) {
            mReportsFragment.onApplyFilter(list)
        }
    }

    private fun buildReportTabs() {
        val viewPagerAdapter = ViewPagerAdapter(this)
        mTransactionFragment =
            TransactionsFragment.newInstance(TransactionsFragment.TAG_EXPENSES)
        mReportsFragment = TransactionsFragment.newInstance(TransactionsFragment.TAG_REPORTS)
        viewPagerAdapter.addFrag(mTransactionFragment, TransactionsFragment.TAG_EXPENSES)
        viewPagerAdapter.addFrag(mReportsFragment, TransactionsFragment.TAG_REPORTS)
        mBinding.reportVp.adapter = viewPagerAdapter
        TabLayoutMediator(
            mBinding.reportTabs,
            mBinding.reportVp
        ) { tab, position ->
            tab.text = viewPagerAdapter.mFragmentTitleList[position]
        }.attach()
        if (mViewModel.isReportSelected) {
            mBinding.reportTabs.getTabAt(1)?.select()
        }
        mBinding.reportTabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mViewModel.isReportSelected = tab?.position == 1
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        mBinding.reportVp.isUserInputEnabled = false
        mBinding.reportTabs.tabRippleColor = null
    }

    override fun onReload() {

    }
}