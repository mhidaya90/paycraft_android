package com.paycraft.ems.transaction_picker

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseView
import com.paycraft.base.BaseViewModel
import com.paycraft.base.LocalError
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.advance.list.Advance
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
class ExpensePickerBottomViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<BaseView>() {

    val mLocalErrors = SingleLiveEvent<LocalError>()

    val mTransactionsList = arrayListOf<Transaction>()
    val mTransactionsSelected = arrayListOf<Transaction>()

    val mTripsList = arrayListOf<Trip>()
    val mTripsSelected = arrayListOf<Trip>()

    val mAdvancesList = arrayListOf<Advance>()
    val mAdvancesSelected = arrayListOf<Advance>()

    var mAdvances = SingleLiveEvent<List<Advance>>()
    var mTotalAdvances = 0

    var mTrips = SingleLiveEvent<List<Trip>>()
    var mTotalTrips = 0

    var mTransactions = SingleLiveEvent<List<Transaction>>()
    var mTotalTransaction = 0

    fun getTrips(page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            val filterMap = HashMap<String, String>()
            filterMap["added_to_report"] = "false"
            filterMap["page"] = page.toString()
            when (val response = apiRepo.getTrips(filterMap)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        if (response.body.success()) {
                            response.body.response?.trips?.let {
                                mTripsList.addAll(it)
                                mTrips.postValue(it)
                            }
                            mTotalTrips = response.body.total()
                        } else {
                            mLocalErrors.postValue(LocalError(response.body.message(), true, true))
                        }
                    }
                }

                is ApiErrorResponse -> {
                    withContext(Dispatchers.Main) {
                        mLocalErrors.postValue(LocalError(response.message, true, true))
                    }
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

    fun getAdvances(page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            val filterMap = HashMap<String, String>()
            filterMap["added_to_report"] = "false"
            filterMap["page"] = page.toString()
            when (val response = apiRepo.getAdvances(filterMap)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        if (response.body.success()) {
                            response.body.response?.advances?.let {
                                mAdvancesList.addAll(it)
                                mAdvances.postValue(it)
                                mTotalAdvances = response.body.total()
                            }
                        } else {
                            mLocalErrors.postValue(LocalError(response.body.message(), true, true))
                        }
                    }
                }

                is ApiErrorResponse -> {
                    withContext(Dispatchers.Main) {
                        mLocalErrors.postValue(LocalError(response.message, true, true))
                    }
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

    fun transactions(page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            val filterMap = mutableMapOf<String, String>()
            filterMap["added_to_report"] = "false"
            filterMap["page"] = page.toString()
            when (val response = apiRepo.getTransactions(filterMap)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        if (response.body.success()) {
                            response.body.response?.transactions?.let {
                                mTransactionsList.addAll(it)
                                mTotalTrips = response.body.total()
                                mTransactions.postValue(it)
                            }
                        } else {
                            mLocalErrors.postValue(
                                LocalError(
                                    response.body.message(),
                                    true,
                                    true
                                )
                            )
                        }
                    }
                }

                is ApiErrorResponse -> {
                    withContext(Dispatchers.Main) {
                        mLocalErrors.postValue(LocalError(response.message, true, true))
                    }
                }

                is ApiSessionExpired -> {
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
        }
    }

}