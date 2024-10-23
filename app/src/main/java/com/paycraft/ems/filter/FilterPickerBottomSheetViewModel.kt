package com.paycraft.ems.filter

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseView
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.transactions.Filters
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
class FilterPickerBottomSheetViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<BaseView>() {
    val mFilters = SingleLiveEvent<List<Filters>>()
    var mCardId: String? = null
    var type: String? = null

    fun filters() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                apiRepo.getTransactionFilters(type ?: "", cardId = mCardId ?: "")) {
                is ApiSuccessResponse -> {
                    if (response.body.success()) {
                        response.body.response?.let {
                            mFilters.postValue(it)
                        }
                    } else {
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
        }
    }
}