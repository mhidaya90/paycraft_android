package com.paycraft.ems.transactions.create

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.transactions.Category
import com.paycraft.ems.transactions.TransactionField
import com.paycraft.ems.transactions.TransactionFile
import com.paycraft.network.ApiErrorResponse
import com.paycraft.network.ApiRepo
import com.paycraft.network.ApiSessionExpired
import com.paycraft.network.ApiSuccessResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CreateTransactionViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<CreateTransactionView>() {
    var category: Category? = null
    var id: String? = null
    var isCardTransaction: Boolean? = null
    var staticFields = SingleLiveEvent<List<TransactionField>>()
    var fields = SingleLiveEvent<List<TransactionField>>()
    var files = SingleLiveEvent<MutableList<TransactionFile>>()
    var deletedFiles = mutableListOf<TransactionFile>()
    var isCategoryChangedByUser = false
    fun getTransactionAttachments() {
        if (id.isNullOrBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getTransactionAttachments(id ?: "")) {
                is ApiSuccessResponse -> {
                    if (!isCategoryChangedByUser) {
                        response.body.response?.transaction?.mFiles
                            ?.let {
                                files.postValue(it as MutableList<TransactionFile>?)
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

    fun getTransactionFields() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                apiRepo.getTransactionFields(id ?: "", category?.id ?: "")) {
                is ApiSuccessResponse -> {
                    if (!isCategoryChangedByUser) {
                        response.body.response?.fields
                            ?.filter { it.isStatic() }
                            ?.let {
                                staticFields.postValue(it)
                            }
                    }
                    response.body.response?.fields
                        ?.filter { !it.isStatic() }
                        ?.let {
                            fields.postValue(it)
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

    fun createTransaction(
        parts: List<MultipartBody.Part>,
        files: List<MultipartBody.Part>,
        deletedFiles: List<MultipartBody.Part>,
        id: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                if (id.isEmpty())
                    apiRepo.createTransaction(parts, files)
                else
                    apiRepo.updateTransaction(id, parts, files, deletedFiles)
            ) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    response.body.response?.transaction?.let {
                        withContext(Dispatchers.Main) {
                            getView()?.onCreateTransactionSuccess(it)
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