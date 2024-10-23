package com.paycraft.base

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class Paginator(var mLinearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    private val TAG = "Paginator"
    private var mIsLoading = false
    private var mTotalRecords = 0
    private var mTotalRecordsLoaded = 0
    private var mPage = 1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
            if (recyclerView.layoutManager === mLinearLayoutManager) {
                val visibleItemCount: Int = mLinearLayoutManager.childCount
                val totalItemCount: Int = mLinearLayoutManager.itemCount
                val firstVisibleItemIndex: Int =
                    mLinearLayoutManager.findFirstVisibleItemPosition()
                Log.d(
                    TAG,
                    "onScrolled: visibleItemCount=${visibleItemCount} " +
                            "firstVisibleItemIndex=${firstVisibleItemIndex} " +
                            "totalItemCount=${totalItemCount}"
                )
                if (visibleItemCount + firstVisibleItemIndex >= totalItemCount) {
                    Log.d(
                        TAG,
                        "onScrolled: visibleItemCount + firstVisibleItemIndex=${visibleItemCount} " +
                                "totalItemCount=${totalItemCount}"
                    )
                    if (!mIsLoading) {
                        mIsLoading = true
                        if (hasMoreToLoad()) {
                            Log.d(
                                TAG,
                                "onScrolled: total=${mTotalRecords} totalLoaded=${mTotalRecordsLoaded} page=${mPage}"
                            )
                            paginate(++mPage)
                        }
                    }
                }
            }
        }
    }

    private fun hasMoreToLoad(): Boolean {
        Log.d(TAG, "hasMoreToLoad: $mTotalRecords > $mTotalRecordsLoaded")
        return mTotalRecords > mTotalRecordsLoaded
    }

    fun pageLoadingCompleted(total: Int, totalLoaded: Int) {
        mTotalRecords = total
        mTotalRecordsLoaded = totalLoaded
        mIsLoading = false
    }

    abstract fun paginate(nextPageIndex: Int)

    fun reset() {
        mIsLoading = false
        mTotalRecords = 0
        mTotalRecordsLoaded = 0
        mPage = 1
        Log.d(
            TAG,
            "reset: total=${mTotalRecords} totalLoaded=${mTotalRecordsLoaded} page=${mPage}"
        )
    }
}

