package com.paycraft.ems

import android.util.Base64
import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
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
class ExpenseManagementViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<ExpenseManagementView>() {
    var isReportSelected = false
    fun card() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = apiRepo.card()) {
                is ApiSuccessResponse -> {
                    response.body.data?.let {
                        val b = Base64.decode(it, Base64.DEFAULT)
                        withContext(Dispatchers.Main) {
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
}