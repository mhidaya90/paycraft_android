package com.paycraft.ems.attachments

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseView
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.transactions.TransactionFile
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
class AttachmentsViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<BaseView>() {
    var files = SingleLiveEvent<MutableList<TransactionFile>>()
    fun getTransactionAttachments(id: String) {
        if (id.isNullOrBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getTransactionAttachments(id ?: "")) {
                is ApiSuccessResponse -> {
                    response.body.response?.transaction?.mFiles
                        ?.let {
                            files.postValue(it as MutableList<TransactionFile>?)
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