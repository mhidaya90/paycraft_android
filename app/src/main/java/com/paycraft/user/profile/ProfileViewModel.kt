package com.paycraft.user.profile

import androidx.lifecycle.viewModelScope
import com.paycraft.home.User
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SessionManager
import com.paycraft.base.SingleLiveEvent
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
class ProfileViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<ProfileView>() {
    var mUser = SingleLiveEvent<User>()

    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                apiRepo.deleteAccount(DeleteAccountRequest(SessionManager.instance().getEmpId()))) {
                is ApiSuccessResponse -> {
                    if (response.body.success()) {
                        withContext(Dispatchers.Main) {
                            if (response.body.success()) {
                                getView()?.onAccountDeleted(response.body.message())
                            } else {
                                getView()?.onAccountDeletionFailed(response.body.message())
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
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
        }
    }

    fun profile() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = apiRepo.profile()) {
                is ApiSuccessResponse -> {
                    if (response.body.success()) {
                        withContext(Dispatchers.Main) {
                            response.body.response?.profile?.let {
                                it.name?.let { name -> SessionManager.instance().setName(name) }
                                it.email?.let { email -> SessionManager.instance().setEmail(email) }
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
                    withContext(Dispatchers.Main) {
                        getView()?.sessionExpired()
                    }
                }
            }
        }
    }

}