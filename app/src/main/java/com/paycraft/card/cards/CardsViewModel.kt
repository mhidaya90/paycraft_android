package com.paycraft.card.cards

import androidx.lifecycle.viewModelScope
import com.paycraft.base.BaseViewModel
import com.paycraft.base.SingleLiveEvent
import com.paycraft.card.cards.balances.TopUpRequest
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
class CardsViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<CardsView>() {
    var mCards = SingleLiveEvent<MutableList<Card>>()
    var mCardError = SingleLiveEvent<Boolean>()
    var mCurrentCard: Int = 0
    var isTransactionsOpened = false

    fun cardTopUp(topUpRequest: TopUpRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.cardTopUp(topUpRequest)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        mCardError.postValue(true)
                        showMessage(response.body.message())
                        return@launch
                    }
                    response.body.response?.paymentGatewayUrl?.let { getView()?.onTopUp(it) }
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

    fun getCards() {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.cards()) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        mCardError.postValue(true)
                        showMessage(response.body.message())
                        return@launch
                    }
                    response.body.response?.cards?.let { cards ->
                        withContext(Dispatchers.Main) {
                            val list = mutableListOf<Card>()
                            list.addAll(cards)
                            mCards.postValue(list)
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


    fun addCardSendOtpAsync(id: String, actionType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            showProgress()
            when (val response = apiRepo.addCardSendOtp(id, actionType)) {
                is ApiSuccessResponse -> {
                    if (!response.body.success()) {
                        showMessage(response.body.message())
                        return@launch
                    }
                    response.body.response?.txnId?.let {
                        withContext(Dispatchers.Main) {
                            getView()?.onOtpSent(it)
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