package com.paycraft.ems

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.databinding.ActivityAdvancesBinding
import com.paycraft.ems.TransactionsFragment.Companion.TAG_ADVANCES
import com.paycraft.ems.TransactionsFragment.Companion.TAG_TRIPS
import com.paycraft.ems.advance.create.CreateAdvanceActivity
import com.paycraft.ems.filter.FilterPickerBottomSheetListener
import com.paycraft.ems.transactions.Filters
import com.paycraft.ems.trip.create.CreateTripActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordsActivity : BaseActivity(), FilterPickerBottomSheetListener,TransactionsFragmentListener {
    companion object {
        const val E_FRAGMENT = "fragment"
        fun start(activity: Activity, fragment: String) {
            Intent(activity, RecordsActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(E_FRAGMENT, fragment)
            }.run {
                activity.startActivityForResult(this, RC_REFRESH_PREVIOUS_SCREEN)
            }
        }
    }

    lateinit var mBinding: ActivityAdvancesBinding
    lateinit var fragment: TransactionsFragment
    var mType: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_advances)
        mType = intent.getStringExtra(E_FRAGMENT)
        mType?.let {
            configToolbar(
                mBinding.toolbarLayout.toolbar,
                mBinding.toolbarLayout.toolbarTitleTv, it
            )
            fragment = TransactionsFragment.newInstance(it)
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.containerFl, fragment)
                commit()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ems, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addRecord -> {
                when (mType) {
                    TAG_TRIPS -> {
                        CreateTripActivity.start(this)
                    }
                    TAG_ADVANCES -> {
                        CreateAdvanceActivity.start(this)
                    }
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
            CreateAdvanceActivity.RC -> {
                fragment.refresh()
            }
            CreateTripActivity.RC -> {
                fragment.refresh()
            }
            RC_REFRESH_PREVIOUS_SCREEN -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                fragment.refresh()
            }
        }
    }

    override fun onApplyFilter(list: List<Filters>) {
        fragment.onApplyFilter(list)
    }

    override fun onReload() {

    }
}