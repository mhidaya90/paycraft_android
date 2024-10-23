package com.paycraft.ems.transactions.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowDisplayCustomFieldBinding
import com.paycraft.ems.transactions.FieldOption

class DisplayFieldAdapter(
    private val mContext: Context,
    private val mList: List<FieldOption>
) :
    RecyclerView.Adapter<DisplayFieldAdapter.DisplayFieldViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayFieldViewModel {
        val mBinding: RowDisplayCustomFieldBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.row_display_custom_field, parent, false
        )
        return DisplayFieldViewModel(mBinding)
    }

    override fun onBindViewHolder(holder: DisplayFieldViewModel, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class DisplayFieldViewModel(val mBinding: RowDisplayCustomFieldBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(fieldOption: FieldOption) {
            mBinding.fieldNameTv.text = fieldOption.key ?: ""
            mBinding.fieldValueTv.text = fieldOption.value ?: ""
        }
    }
}