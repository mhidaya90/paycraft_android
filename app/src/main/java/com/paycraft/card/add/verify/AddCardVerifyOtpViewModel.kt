package com.paycraft.card.add.verify

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
class AddCardVerifyOtpViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<AddCardVerifyOtpView>() {

    fun addCardVerifyOtp(id: String, otp: String, trxId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.addCardVerifyOtp(id, otp, trxId)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        showMessage(response.body.message())
                    } else {
                        withContext(Dispatchers.Main) {
                            activateCard(id)
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

    private fun activateCard(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.addCardActivate(id)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        if (!response.body.success()) {
                            showMessage(response.body.message())
                        } else {
                            response.body.response?.let { getView()?.onActivateCard(it) }
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