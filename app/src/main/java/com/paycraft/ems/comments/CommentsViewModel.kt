package com.paycraft.ems.comments

import androidx.lifecycle.viewModelScope
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
class CommentsViewModel @Inject constructor(val apiRepo: ApiRepo) : BaseViewModel<CommentsView>() {
    val comments = SingleLiveEvent<MutableList<Comment>>()

    fun comments(mRecordType: String, id: String, page: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = when (mRecordType) {
                "Advance" -> apiRepo.advanceComments(id)
                "Report" -> apiRepo.reportComments(id)
                "Trip" -> apiRepo.tripComments(id)
                else -> apiRepo.transactionComments(id, page)
            }) {
                is ApiSuccessResponse -> {
                    response.body.response?.comments?.let {
                        comments.postValue(it as MutableList<Comment>)
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