package com.paycraft.ems.reports.detail

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.advance.detail.LinkAdvancesToRequest
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.reports.Report
import com.paycraft.ems.transaction_picker.LinkTransactionsToRequest
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.trip.create.Trip
import com.paycraft.ems.trip.details.LinkTripsToRequest
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
class ReportViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<ReportView>() {
    var isSubmit = false
    lateinit var mId: String
    lateinit var mReport: Report
    var mViolations = SingleLiveEvent<List<DisplayViolation>>()
    val mSelectedTransactionsToInclude = arrayListOf<Transaction>()

    fun getReport() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getReport(mId)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        response.body.response?.report?.let { getView()?.reportSuccess(it) }
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

    fun getTransaction(id: String, rid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getTransaction(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        response.body.response?.transaction?.let {
                            getView()?.onTransactionSuccess(it, rid)
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

    fun validateTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            val ids = if (isSubmit)
                mReport.transactions?.map { it.id ?: "" }?.toList() ?: arrayListOf()
            else
                mSelectedTransactionsToInclude.map { it.id ?: "" }.toList()

            when (val response = apiRepo.validateTransaction(
                ValidateTransactionRequest(
                    if (isSubmit) mId else null,
                    ids
                )
            )) {
                is ApiSuccessResponse -> {
                    val list: List<DisplayViolation> =
                        response.body.response?.getViolationList() ?: arrayListOf()
                    if (list.any { it.isBlocked == true } || list.any { it.isWarning == true }) {
                        mViolations.postValue(list)
                    } else {
                        submitOrLink()
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

    fun submitOrLink() {
        if (isSubmit) {
            submitReport(mId)
        } else {
            linkTransactionToReport(
                LinkTransactionsToRequest(
                    mId,
                    mSelectedTransactionsToInclude.map { it.id ?: "" }.toList()
                )
            )
        }
    }

    fun linkTransactionToReport(request: LinkTransactionsToRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.linkTransactionToReport(request)) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    response.body.response?.report?.let {
                        withContext(Dispatchers.Main) {
                            getView()?.onTransactionsLinkedSuccess(it)
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

    fun linkTripToReport(trips: List<Trip>) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.linkTripsToReport(
                LinkTripsToRequest(
                    mId,
                    trips.map { it.id ?: "" })
            )) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        if (response.body.success()) {
                            getView()?.onTripLinkingSuccess(trips)
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

    fun linkAdvanceToReport(advances: List<Advance>) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.linkAdvanceToReport(
                LinkAdvancesToRequest(
                    mId,
                    advances.map { it.id ?: "" })
            )) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        if (response.body.success()) {
                            getView()?.onAdvanceLinkingSuccess(advances)
                        }
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

    fun createReportComment(id: String, comment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.createReportComment(id, comment)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        getView()?.onCreateReportCommentSuccess()
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

    fun recallReport(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.recallReport(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        getView()?.onRecallReportSuccess()
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

    fun submitReport(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.submitReport(id)) {
                is ApiSuccessResponse -> {
                    if (response.body.status == true) {
                        withContext(Dispatchers.Main) {
                            getView()?.onReportSubmittedSuccess()
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