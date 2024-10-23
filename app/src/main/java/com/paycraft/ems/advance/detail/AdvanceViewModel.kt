package com.paycraft.ems.advance.detail

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.reports.Report
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
class AdvanceViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<AdvanceView>() {
    var mReport = SingleLiveEvent<List<Report>>()
    lateinit var mAdvance: Advance
    lateinit var id: String
    fun linkAdvanceToReport(request: LinkAdvancesToRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.linkAdvanceToReport(request)) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    if (response.body.success()) {
                        withContext(Dispatchers.Main) {
                            getView()?.onAdvanceLinkingSuccess()
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

    fun getAdvance() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getAdvance(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        response.body.response?.advance?.firstOrNull()?.let {
                            getView()?.onAdvanceSuccess(it)
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

    fun submitAdvance() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.submitAdvance(id)) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    if (response.body.success()) {
                        withContext(Dispatchers.Main) {
                            getView()?.onAdvanceSubmittedSuccess()
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

    fun deleteAdvance() {
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

    fun createAdvanceComment(comment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.createAdvanceComment(id, comment)) {
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

    fun recallAdvance() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.recallAdvance(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        getView()?.onRecallAdvanceSuccess()
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