package com.paycraft.card.add.otp

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
class AddCardViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<AddCarView>() {

    fun cardRegister(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.cardRegister(id)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        showMessage(response.body.message())
                    } else {
                        addCardSendOtpAsync(id)
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


    private fun addCardSendOtpAsync(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response =
                apiRepo.addCardSendOtp(id, NEW_CUSTOMER_REGISTRATION)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        showMessage(response.body.message())
                    } else {
                        response.body.response?.txnId?.let {
                            withContext(Dispatchers.Main) {
                                getView()?.onOtpSent(it)
                            }
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