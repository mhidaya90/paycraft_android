package com.paycraft.ems.advance.create

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
class CreateAdvanceViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<CreateAdvanceView>() {
    var fields = SingleLiveEvent<List<TransactionField>>()

    fun getAdvanceFields(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getAdvanceFields(id)) {
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

    fun createAdvance(req: List<FieldOption>, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                if (id.isEmpty())
                    apiRepo.createAdvance(TransactionRequest(req))
                else
                    apiRepo.updateAdvance(id, TransactionRequest(req))
            ) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    response.body.response?.advance?.let {
                        withContext(Dispatchers.Main) {
                            getView()?.onCreateAdvanceSuccess(it)
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