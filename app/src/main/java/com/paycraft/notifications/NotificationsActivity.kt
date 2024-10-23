package com.paycraft.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paginate.Paginate
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.databinding.ActivityNotificationsBinding
import com.paycraft.ems.advance.detail.AdvanceActivity
import com.paycraft.ems.reports.detail.ReportActivity
import com.paycraft.ems.transactions.detail.TransactionActivity
import com.paycraft.ems.trip.details.TripActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsActivity : BaseActivity(), NotificationAdapterListener {
    lateinit var mBinding: ActivityNotificationsBinding

    companion object {
        fun start(context: Context) {
            Intent(context, NotificationsActivity::class.java)
                .apply {
                    this.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.run {
                    context.startActivity(this)
                }
        }
    }

    lateinit var mViewModel: NotificationsViewModel
    var mNotificationsAdapter: NotificationAdapter? = null
    var mLinearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notifications)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv,
            getString(R.string.title_notifications)
        )

        mViewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]
        mViewModel.setView(this)

        mBinding.notificationsRv.let {
            mNotificationsAdapter = NotificationAdapter(arrayListOf())
            it.adapter = mNotificationsAdapter
            mLinearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            it.layoutManager = mLinearLayoutManager
        }

        mViewModel.mNotifications.observe(this) {
            mNotificationsAdapter?.notifyWithData(it)
            mViewModel.loading = false
        }

        mBinding.srl.setOnRefreshListener {
            mViewModel.mNotifications.postValue(arrayListOf())
            mBinding.srl.isRefreshing = false
            mViewModel.page = 1
            mViewModel.loading = false
            doApi()
        }

        Paginate.with(mBinding.notificationsRv, object : Paginate.Callbacks {
            override fun onLoadMore() {
                mViewModel.loading = true
                mViewModel.notifications()
            }

            override fun isLoading(): Boolean {
                return mViewModel.loading
            }

            override fun hasLoadedAllItems(): Boolean {
                return mViewModel.totalNotifications <= (mViewModel.mNotifications.value?.size ?: 0)
            }
        }).setLoadingTriggerThreshold(1)
            .addLoadingListItem(false)
            .build()

        doApi()
    }

    private fun doApi() {
        mViewModel.notifications()
    }


    override fun onClickNotification(n: PaycraftNotification) {
        n.id?.let { mViewModel.markNotificationAsSeen(it) }
        n.data?.id?.let { id ->
            when (n.data.page) {
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

                else -> {
                }
            }
        }
    }
}