package com.paycraft.user.forgot

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
class ForgotPasswordViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<ForgotPasswordView>() {

    fun forgotPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.forgotPasswordAsync(email)) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        if (!response.body.success()) {
                            getView()?.showStaticError(response.body.message())
                        } else {
                            getView()?.onForgotPasswordSuccess()
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