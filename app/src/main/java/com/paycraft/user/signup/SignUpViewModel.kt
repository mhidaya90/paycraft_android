package com.paycraft.user.signup

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
class SignUpViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<SignUpView>() {
    var showPassword = false

    fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.signUp(name, email, phone, password)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        withContext(Dispatchers.Main) {
                            getView()?.showStaticError(response.body.message())
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            getView()?.onSignUpSuccess()
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