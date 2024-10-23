package com.paycraft.ems.report_expenses

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
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
class ReportExpensesViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<ReportExpensesView>() {
    var mReport: Report? = null
    fun transactions(page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {

            val filterMap = mutableMapOf<String, String>()
            if (mReport?.id?.isNotEmpty() == true)
                filterMap["report_id"] = mReport?.id ?: ""
            filterMap["page"] = page.toString()

            when (val response = apiRepo.getTransactions(filterMap)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        response.body.response?.transactions?.let {
                            getView()?.onReportTransactionsSuccess(it)
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

    fun unlintExpense(tId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                apiRepo.unlinkTransactionsAsync(mReport?.id ?: "", tId)) {
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
}