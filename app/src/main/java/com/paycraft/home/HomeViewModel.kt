package com.paycraft.home

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SessionManager
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.reports.Report
import com.paycraft.ems.transactions.Transaction
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
class HomeViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<HomeView>() {
    var mUser = SingleLiveEvent<User>()
    var mTransactions = SingleLiveEvent<List<Transaction>>()
    var mReports = SingleLiveEvent<List<Report>>()
    var id: String? = null
    var page: String? = null
    var isMenuOpened = false

    fun profile() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = apiRepo.profile()) {
                is ApiSuccessResponse -> {
                    if (response.body.success()) {
                        response.body.response?.profile?.let {
                            it.id?.let { name -> SessionManager.instance().setName(name) }
                            it.name?.let { name -> SessionManager.instance().setName(name) }
                            it.email?.let { email -> SessionManager.instance().setEmail(email) }
                            withContext(Main) {
                                mUser.postValue(it)
                            }
                        }
                    } else {
                        showMessage(response.body.message())
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
                }

                is ApiSessionExpired -> {
                    withContext(Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
        }
    }

    fun transactions() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getTransactions()) {
                is ApiSuccessResponse -> {
                    withContext(Main) {
                        if (response.body.success()) {
                            response.body.response?.transactions?.take(3).let {
                                mTransactions.postValue(it)
                            }
                        } else {
                            showMessage(response.body.message())
                        }
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
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

    fun reports() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.getReportsAsync()) {
                is ApiSuccessResponse -> {
                    withContext(Main) {
                        if (response.body.success()) {
                            response.body.response?.reports?.take(3).let {
                                mReports.postValue(it)
                            }
                        } else {
                            showMessage(response.body.message())
                        }
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
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

    fun addCardSendOtpAsync(id: String, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.addCardSendOtp(id, type)) {
                is ApiSuccessResponse -> {
                    withContext(Main) {
                        if (response.body.success()) {
                            response.body.response?.txnId?.let {
                                getView()?.onOtpSent(id, it)
                            }
                        } else {
                            showMessage(response.body.message())
                        }
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
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

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.logout()) {
                is ApiSuccessResponse -> {
                    if (response.body.success()) {
                        withContext(Main) {
                            getView()?.sessionExpired()
                        }
                    } else {
                        showMessage(response.body.message())
                    }
                }

                is ApiErrorResponse -> {
                    showMessage(response.message)
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
