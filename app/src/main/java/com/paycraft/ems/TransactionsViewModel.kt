package com.paycraft.ems

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.reports.Report
import com.paycraft.ems.transactions.Filter
import com.paycraft.ems.transactions.Filters
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.trip.create.Trip
import com.paycraft.network.ApiErrorResponse
import com.paycraft.network.ApiRepo
import com.paycraft.network.ApiSessionExpired
import com.paycraft.network.ApiSuccessResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(val apiRepo: ApiRepo) :
    BaseViewModel<TransactionsView>() {
    var mOsSearchFilterEnabled = true
    var apiRefresh = false
    var mTag = ""
    var mCardId = ""
    var mReportId = ""
    var mSelectedFilters: List<Filters>? = null
    var mTotalRecords = 0
    val mTransactions = SingleLiveEvent<MutableList<Transaction>>()
    val mTrips = SingleLiveEvent<MutableList<Trip>>()
    val mAdvances = SingleLiveEvent<MutableList<Advance>>()
    val mReports = SingleLiveEvent<MutableList<Report>>()

    var mDeleteConfirmationTransaction: Transaction? = null
    var mDeleteConfirmationTrip: Trip? = null
    var mDeleteConfirmationAdvance: Advance? = null
    var mDeleteConfirmationReport: Report? = null
    var mSearchKey: String = ""

    fun transactions(page: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            val filterMap = prepareFilters()
            if (apiRefresh)
                filterMap["refresh"] = apiRefresh.toString()
            if (mCardId.isNotEmpty()) {
                filterMap["card_id"] = mCardId
                filterMap["sort_key"] = "transaction_date"
            }
            if (mReportId.isNotEmpty())
                filterMap["report_id"] = mReportId
            filterMap["page"] = page.toString()
            filterMap["search_key"] = mSearchKey
            filterMap["source"] = "mobile"
            when (val response =
                apiRepo.getTransactions(filterMap, mCardId.isNotEmpty())) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        if (!response.body.success()) {
                            showMessage(response.body.message())
                            return@withContext
                        }
                        mTotalRecords = response.body.totalRecords ?: 0
                        response.body.response?.transactions?.let {
                            if (null == mTransactions.value)
                                mTransactions.value = mutableListOf()
                            mTransactions.value?.addAll(it)
                            mTransactions.postValue(mTransactions.value)
                            Log.d(
                                "TransactionsViewModel",
                                "notifications: totalNotifications : ${mTotalRecords} > Loaded : ${(mTransactions.value?.size ?: 0)}"
                            )
                        }
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
        }
    }

    fun getTrips(page: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            val filterMap = prepareFilters()
            if (mReportId.isNotEmpty())
                filterMap["report_id"] = mReportId
            filterMap["page"] = page.toString()
            filterMap["search_key"] = mSearchKey
            when (val response = apiRepo.getTrips(filterMap)) {
                is ApiSuccessResponse -> {
                    mTotalRecords = response.body.totalRecords ?: 0
                    withContext(Dispatchers.Main) {
                        response.body.response?.trips?.let {
                            if (null == mTrips.value)
                                mTrips.value = mutableListOf()
                            mTrips.value?.addAll(it)
                            mTrips.postValue(mTrips.value)
                        }
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
        }
    }

    fun getAdvances(page: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            val filterMap = prepareFilters()
            if (mReportId.isNotEmpty())
                filterMap["report_id"] = mReportId
            filterMap["page"] = page.toString()
            filterMap["search_key"] = mSearchKey
            when (val response = apiRepo.getAdvances(filterMap)) {
                is ApiSuccessResponse -> {
                    mTotalRecords = response.body.totalRecords ?: 0
                    withContext(Dispatchers.Main) {
                        response.body.response?.advances?.let {
                            if (null == mAdvances.value)
                                mAdvances.value = mutableListOf()
                            mAdvances.value?.addAll(it)
                            mAdvances.postValue(mAdvances.value)
                        }
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
        }
    }

    fun reports(page: Int = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            val filterMap = prepareFilters()
            if (mReportId.isNotEmpty())
                filterMap["report_id"] = mReportId
            filterMap["page"] = page.toString()
            filterMap["search_key"] = mSearchKey
            when (val response = apiRepo.getReportsAsync(filterMap)) {
                is ApiSuccessResponse -> {
                    mTotalRecords = response.body.totalRecords ?: 0
                    withContext(Dispatchers.Main) {
                        response.body.response?.reports?.let {
                            if (null == mReports.value)
                                mReports.value = mutableListOf()
                            mReports.value?.addAll(it)
                            mReports.postValue(mReports.value)
                        }
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
        }
    }

    fun deleteTrip(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.deleteTrip(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        getView()?.onDeleteTripSuccess()
                    }
                    showMessage(response.body.message())
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
            dismissProgress()
        }
    }

    fun deleteAdvance(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.deleteAdvance(id)) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    withContext(Dispatchers.Main) {
                        getView()?.onDeleteAdvanceSuccess()
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
            dismissProgress()
        }
    }

    fun deleteReport(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.deleteReport(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        getView()?.onDeleteReportSuccess()
                    }
                    showMessage(response.body.message())
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
            dismissProgress()
        }
    }

    private fun prepareFilters(): MutableMap<String, String> {
        val filterMap = mutableMapOf<String, String>()
        mSelectedFilters
            ?.filter {
                (!it.key.isNullOrEmpty()) && (it.filters?.any { it.isSelected } ?: false)
            }?.associateTo(filterMap) {
                (it.key ?: "") to (
                        it.filters
                            ?.filter { it.isSelected }
                            ?.joinToString(",")
                            {
                                it.status ?: ""
                            }
                            ?: "")
            }
        return filterMap;
    }

    fun deleteTransaction() {
        if (null == mDeleteConfirmationTransaction?.id) {
            getView()?.showToast("No transaction selected to delete!")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                apiRepo.deleteTransaction(mDeleteConfirmationTransaction?.id ?: "")) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        getView()?.onDeleteTransactionSuccess()
                    }
                    showMessage(response.body.message())
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
            dismissProgress()
        }
    }

    fun unlintExpense() {
        if (null == mDeleteConfirmationTransaction?.id) {
            getView()?.showToast("No transaction selected to Unlink!")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                apiRepo.unlinkTransactionsAsync(
                    mReportId,
                    mDeleteConfirmationTransaction?.id!!
                )) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    withContext(Dispatchers.Main) {
                        getView()?.onUnLinkTransactionsSuccess()
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
            dismissProgress()
        }
    }

    fun unlintTrip() {
        if (null == mDeleteConfirmationTrip?.id) {
            getView()?.showToast("No trip selected to Unlink!")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                apiRepo.unlinkTripAsync(
                    mReportId,
                    mDeleteConfirmationTrip?.id!!
                )) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    withContext(Dispatchers.Main) {
                        getView()?.onUnLinkTripSuccess()
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
            dismissProgress()
        }
    }

    fun unlintAdvance() {
        if (null == mDeleteConfirmationAdvance?.id) {
            getView()?.showToast("No advance selected to Unlink!")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                apiRepo.unlinkAdvanceAsync(
                    mReportId,
                    mDeleteConfirmationAdvance?.id!!
                )) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    withContext(Dispatchers.Main) {
                        getView()?.onUnLinkAdvanceSuccess()
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
            dismissProgress()
        }
    }

    fun filtersMapString(): MutableList<Filter>? {
        val filters = mSelectedFilters
            ?.filter {
                (!it.key.isNullOrEmpty()) && (it.filters?.any { it.isSelected } ?: false)
            }
            ?.flatMap { it.filters!! }
            ?.filter { it.isSelected }?.toMutableList()
        return if (filters.isNullOrEmpty())
            mutableListOf(Filter("All", "All"))
        else
            filters
    }

    fun unselectFilter(filter: Filter) {
        mSelectedFilters?.let {
            for (a in it) {
                a.filters
                    ?.find { it.status == filter.status }
                    ?.apply {
                        this.isSelected = !this.isSelected
                    }
            }
        }
        "a".groupBy { "s" }
    }
}