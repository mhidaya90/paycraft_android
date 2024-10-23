package com.paycraft.ems.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowHistoryBinding

class HistoryAdapter(
    val mContext: Context,
    var mList: List<History>
) : RecyclerView.Adapter<HistoryAdapter.ExpenseDataPickerViewModel>() {
    fun notifyWithData(comments: List<History>) {
        mList = comments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseDataPickerViewModel {
        val mBinding: RowHistoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.row_history, parent, false
        )
        return ExpenseDataPickerViewModel(mBinding)
    }

    override fun onBindViewHolder(holder: ExpenseDataPickerViewModel, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class ExpenseDataPickerViewModel(val mBinding: RowHistoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(comment: History) {
            mBinding.commentTv.text = comment.body ?: ""
            mBinding.nameTv.text = comment.userName ?: ""
            mBinding.timeTv.text = comment.createdAt ?: ""
            mBinding.actionTv.text = comment.status ?: ""
        }
    }
}