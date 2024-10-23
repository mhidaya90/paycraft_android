package com.paycraft.notifications

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseView
import com.paycraft.base.BaseViewModel
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
class NotificationsViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<BaseView>() {
    var mNotifications = SingleLiveEvent<MutableList<PaycraftNotification>>()
    var totalNotifications: Int = 0
    var page = 1
    var loading = false

    init {
        mNotifications.value = mutableListOf()
    }

    fun notifications() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.notifications(page++)) {
                is ApiSuccessResponse -> {
                    if (response.body.success()) {
                        totalNotifications = response.body.totalRecords ?: 0
                        withContext(Dispatchers.Main) {
                            response.body.response.notifications?.let {
                                mNotifications.value?.addAll(it)
                                mNotifications.postValue(mNotifications.value)
                                Log.d(
                                    "NotificationsViewModel",
                                    "notifications: totalNotifications : ${totalNotifications} > Loaded : ${(mNotifications.value?.size ?: 0)}"
                                )
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
            dismissProgress()
        }
    }

    fun markNotificationAsSeen(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.markNotificationAsSeen(id)) {
                is ApiSuccessResponse -> {
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