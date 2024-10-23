package com.paycraft.ems.reports.create

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.TransactionField
import com.paycraft.ems.transactions.TransactionRequest
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
class CreateReportViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<CreateReportView>() {
    var fields = SingleLiveEvent<List<TransactionField>>()
    fun getReportFields(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getReportFields(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        response.body.response?.fields?.let {
                            fields.postValue(it)
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

    fun createReport(req: List<FieldOption>, id: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                if (id.isEmpty())
                    apiRepo.createReport(TransactionRequest(req))
                else
                    apiRepo.updateReport(id, TransactionRequest(req))
            ) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    response.body.response?.report?.let {
                        withContext(Dispatchers.Main) {
                            getView()?.onCreateReportSuccess(it)
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
}