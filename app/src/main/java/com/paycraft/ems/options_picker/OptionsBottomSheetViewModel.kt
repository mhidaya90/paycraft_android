package com.paycraft.ems.options_picker

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseView
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.transactions.Category
import com.paycraft.ems.transactions.FieldOption
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
class OptionsBottomSheetViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<BaseView>() {
    var category: List<Category>? = null
    var mList = SingleLiveEvent<List<FieldOption>>()
    var mType: String = ""
    var title: String = ""
    var isLive: Boolean = false

    fun createMerchant(merchantName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = apiRepo.createMerchant(merchantName)) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    if (response.body.success()) {
                        getMerchants(merchantName)
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

    fun getMerchants(searchKey: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = apiRepo.getMerchants(searchKey)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        showMessage(response.body.message())
                    } else {
                        response.body.response
                            ?.filter { it.enable ?: false }
                            ?.map {
                                FieldOption(it.id, it.name)
                            }.let {
                                mList.postValue(it)
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

    fun categories() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.categories()) {
                is ApiSuccessResponse -> {
                    category = response.body.response?.categories
                    response.body.response?.categories?.filter {
                        it.isEnable ?: false
                    }?.map {
                        FieldOption(it.id, it.name)
                    }.let {
                        mList.postValue(it)
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

    fun categoryObj(fieldOption: FieldOption): Category? {
        return category?.find { it.id == fieldOption.id }
    }
}