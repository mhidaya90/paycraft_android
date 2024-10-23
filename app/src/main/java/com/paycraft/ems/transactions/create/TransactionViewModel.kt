package com.paycraft.ems.transactions.create

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.reports.detail.DisplayViolation
import com.paycraft.ems.reports.detail.ValidateTransactionRequest
import com.paycraft.ems.transaction_picker.LinkTransactionsToRequest
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.transactions.detail.TransactionView
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
class TransactionViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<TransactionView>() {
    lateinit var id: String
    lateinit var mTransaction: Transaction
    var mViolations = SingleLiveEvent<List<DisplayViolation>>()
    fun getTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getTransaction(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        response.body.response?.transaction?.let {
                            getView()?.onTransactionSuccess(it)
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

    fun createTransactionComment(comment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.createTransactionComment(id, comment)) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    withContext(Dispatchers.Main) {
                        getView()?.onCreateReportCommentSuccess()
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

    fun reports(page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            val filterMap = HashMap<String, String>()
            filterMap["status"] = "pending"
            filterMap["page"] = page.toString()
            when (val response =
                apiRepo.getReportsAsync(filterMap)) {
                is ApiSuccessResponse -> {
                    response.body.response?.reports?.let {
                        withContext(Dispatchers.Main) {
                            getView()?.onUnSubmittedReportsSuccess(it)
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

    fun linkTransactionToReport(request: LinkTransactionsToRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.linkTransactionToReport(request)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        showMessage(response.body.message())
                        getView()?.onTransactionLinedToReport()
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

    fun deleteTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.deleteTransaction(id)) {
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

    fun validateTransaction(request: LinkTransactionsToRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.validateTransaction(
                ValidateTransactionRequest(
                    null,
                    request.transactions
                )
            )) {
                is ApiSuccessResponse -> {
                    val list: List<DisplayViolation> =
                        response.body.response?.getViolationList() ?: arrayListOf()
                    if (list.any { it.isBlocked == true } || list.any { it.isWarning == true }) {
                        mViolations.postValue(list)
                    } else {
                        linkTransactionToReport(request)
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