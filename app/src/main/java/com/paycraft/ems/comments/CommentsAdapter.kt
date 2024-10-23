package com.paycraft.ems.comments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowCommentBinding

class CommentsAdapter(
    val mContext: Context,
    var mList: List<Comment>
) : RecyclerView.Adapter<CommentsAdapter.ExpenseDataPickerViewModel>() {
    fun notifyWithData(comments: List<Comment>) {
        mList = comments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseDataPickerViewModel {
        val mBinding: RowCommentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.row_comment, parent, false
        )
        return ExpenseDataPickerViewModel(mBinding)
    }

    override fun onBindViewHolder(holder: ExpenseDataPickerViewModel, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class ExpenseDataPickerViewModel(val mBinding: RowCommentBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(comment: Comment) {
            mBinding.commentTv.text = comment.body ?: ""
            mBinding.nameTv.text = comment.userName ?: ""
            mBinding.timeTv.text = comment.createdAt ?: ""
        }
    }
}