package com.paycraft.card.cards.settings

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.ems.transactions.FieldOption
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
class CardSettingsViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<CardSettingsView>() {
    val mReasons = SingleLiveEvent<List<FieldOption>>()

    fun blockCardReasons(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.blockCardReasons(id)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        showMessage(response.body.message())
                    } else {
                        withContext(Dispatchers.Main) {
                            mReasons.postValue(response.body.response?.map {
                                FieldOption(it.reasons, it.reasons)
                            } ?: emptyList())
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

    fun blockCard(id: String, reason: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.blockCard(id, reason)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        showMessage(response.body.message())
                    } else {
                        withContext(Dispatchers.Main) {
                            getView()?.onBlockCard()
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

    fun changePosPin(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.setPin(id)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        showMessage(response.body.message())
                        return@launch
                    }
                    response.body.response?.url?.let {
                        withContext(Dispatchers.Main) {
                            getView()?.onSetPinSuccess(it)
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
