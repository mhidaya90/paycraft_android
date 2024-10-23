package com.paycraft.card.cart_number

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
class UpdateCardNumberViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<UpdateCardNumberView>() {
    var id: String? = null
    fun updateCardNumber(mobile: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.updateCardMobileNumber(id ?: "", mobile)) {
                is ApiSuccessResponse -> {
                    showMessage(response.body.message())
                    if (response.body.success()) {
                        withContext(Dispatchers.Main) {
                            getView()?.onUpdateCardNumberSuccess()
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