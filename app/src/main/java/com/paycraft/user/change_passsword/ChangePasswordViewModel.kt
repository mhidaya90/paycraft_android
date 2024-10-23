package com.paycraft.user.change_passsword

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
class ChangePasswordViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<ChangePasswordView>() {
    var currentShowPassword = false
    var showPassword = false
    var confirmShowPassword = false
    fun changePassword(
        currentPassword: String,
        password: String,
        passwordConfirmation: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.changePasswordAsync(
                currentPassword,
                password,
                passwordConfirmation
            )) {
                is ApiSuccessResponse -> {
                    withContext(Dispatchers.Main) {
                        if (response.body.success()) {
                            getView()?.onChangePasswordSuccess()
                        }
                    }
                    showMessage(response.body.message())
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