package com.paycraft.user.login

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SessionManager
import com.paycraft.network.ApiErrorResponse
import com.paycraft.network.ApiRepo
import com.paycraft.network.ApiSessionExpired
import com.paycraft.network.ApiSuccessResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<LoginView>() {
    var showPassword = false
    fun signIn(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.login(email, password)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        withContext(Main) {
                            getView()?.showStaticError(response.body.message())
                        }
                    } else {
                        response.body.employeeId?.let { SessionManager.instance().setEmpId(it) }
                        response.body.authToken?.let { SessionManager.instance().setHeader(it) }
                        response.body.baseCurrency?.symbol?.let {
                            SessionManager.instance().setCurrency(it)
                        }
                        withContext(Main) {
                            getView()?.onLoginSuccess()
                        }
                    }
                }

                is ApiErrorResponse -> {
                    withContext(Main) {
                        getView()?.showStaticError(response.message)
                    }
                }

                is ApiSessionExpired -> {
                    withContext(Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
            dismissProgress()
        }
    }
}