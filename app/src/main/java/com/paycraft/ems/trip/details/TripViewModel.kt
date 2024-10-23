package com.paycraft.ems.trip.details

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.reports.Report
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
class TripViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<TripView>() {
    lateinit var id: String
    lateinit var mTrip: Trip
    var mReport = SingleLiveEvent<List<Report>>()
    fun getTrip() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getTrip(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        response.body.response?.trips?.firstOrNull()?.let {
                            getView()?.onTripSuccess(it)
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
            dismissProgress()
        }
    }

    fun reports(page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            val filterMap = HashMap<String, String>()
            filterMap["status"] = "unsubmitted"
            filterMap["page"] = page.toString()
            when (val response =
                apiRepo.getReportsAsync(filterMap)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        response.body.response?.reports?.let {
                            mReport.postValue(it)
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
            dismissProgress()
        }
    }

    fun submitTrip() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.submitTrip(id)) {
                is ApiSuccessResponse -> {
                    if (response.body.success()) {
                        withContext(Dispatchers.Main) {
                            getView()?.onTripSubmittedSuccess()
                        }
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

    fun linkTripToReport(request: LinkTripsToRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.linkTripsToReport(request)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        if (response.body.success()) {
                            getView()?.onTripLinkingSuccess()
                        }
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

    fun deleteTrip() {
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

    fun createTripComment(comment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.createTripComment(id, comment)) {
                is ApiSuccessResponse -> {
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

    fun recallTrip() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.recallTrip(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        getView()?.onRecallTripSuccess()
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

    fun closeTrip() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.closeTrip(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        getView()?.closeTripSuccess()
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

    fun cancelTrip() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.cancelTrip(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        getView()?.cancelTripSuccess()
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
}