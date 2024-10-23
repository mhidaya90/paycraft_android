package com.paycraft.ems.trip.create

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.transactions.TransactionField
import com.paycraft.ems.transactions.TripRequest
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
class CreateTripViewModel @Inject constructor(val apiRepo: ApiRepo) :
    BaseViewModel<CreateTripView>() {
    var fields = SingleLiveEvent<List<TransactionField>>()
    var transactionRequest: TripRequest = TripRequest(arrayListOf())

    fun getTripFields(id: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getTripFields(id ?: "")) {
                is ApiSuccessResponse -> {
                    if (response.body.success()) {
                        response.body.response?.fields.let {
                            fields.postValue(it)
                        }
                        id?.let {
                            getTrip(it)
                        }
                    } else {
                        showMessage(response.body.message())
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

    private fun getTrip(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getTrip(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        transactionRequest.apply {
                            response.body.response?.trips?.firstOrNull()?.let { t ->
                                flightReservations = t.flightReservations
                                hotelReservations = t.hotelReservations
                                carRentals = t.carRentals
                                otherBookings = t.otherBookings
                            }
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

    fun createTrip(req: TripRequest, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                if (id.isEmpty())
                    apiRepo.createTrip(req)
                else
                    apiRepo.updateTrip(id, req)
            ) {
                is ApiSuccessResponse -> {
                    if (response.body.success()) {
                        response.body.response?.trip?.let {
                            withContext(Dispatchers.Main) {
                                getView()?.onCreateTripSuccess(it)
                            }
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
}