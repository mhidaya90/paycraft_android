package com.paycraft.base

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

open class BaseViewModel<V : BaseView> : ViewModel() {
    private var mView: WeakReference<V>? = null
    val mMutex = Mutex()
    private var mProgressTaskCount = false

    var mError = SingleLiveEvent<LocalError>()

    open fun setView(view: V) {
        this.mView = WeakReference(view)
    }

    open fun getView(): V? {
        return mView?.get()
    }

    suspend fun showMessage(message: String?) {
        withContext(Dispatchers.Main) {
            getView()?.showToast(message ?: "Something went wrong!")
        }
    }

    open suspend fun showProgress() {
        mMutex.withLock {
            if (mProgressTaskCount) {
                return
            }
            mProgressTaskCount = true
        }
        withContext(Dispatchers.Main) {
            getView()?.showProgress()
        }
        Log.d("BaseViewModel", "showProgress: $mProgressTaskCount")
    }

    open suspend fun dismissProgress() {
        mMutex.withLock {
            if (!mProgressTaskCount) {
                return
            }
            mProgressTaskCount = false
        }

        withContext(Dispatchers.Main) {
            getView()?.dismissProgress()
        }
        Log.d("BaseViewModel", "dismissProgress: " + mProgressTaskCount)
    }
}