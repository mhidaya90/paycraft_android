package com.paycraft.ems.history

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
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
class HistoryViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<HistoryView>() {
    val history = SingleLiveEvent<MutableList<History>>()

    fun history(mRecordType: String, id: String, page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = when (mRecordType) {
                "Advance" -> apiRepo.advanceHistory(id)
                "Report" -> apiRepo.reportHistory(id)
                "Trip" -> apiRepo.tripHistory(id)
                else -> apiRepo.transactionHistory(id, page)
            }) {
                is ApiSuccessResponse -> {
                    response.body.response?.history?.let {
                        history.postValue(it as MutableList<History>)
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
}