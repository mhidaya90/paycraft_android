package com.paycraft.ems.options_picker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowFieldOptionBinding
import com.paycraft.ems.transactions.FieldOption

class OptionsAdapter(
    val mContext: Context,
    val mOptionsListener: OptionsListener,
    val mList: List<FieldOption>
) : RecyclerView.Adapter<OptionsAdapter.ExpenseDataPickerViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseDataPickerViewModel {
        val mBinding: RowFieldOptionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.row_field_option, parent, false
        )
        return ExpenseDataPickerViewModel(mBinding)
    }

    override fun onBindViewHolder(holder: ExpenseDataPickerViewModel, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class ExpenseDataPickerViewModel(val mBinding: RowFieldOptionBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {

        init {
            mBinding.root.setOnClickListener(this)
        }

        fun bind(fieldOption: FieldOption) {
            mBinding.optionNameTv.text = fieldOption.value ?: ""
        }

        override fun onClick(v: View?) {
            mOptionsListener.onOptionSelected("", mList[layoutPosition])
        }
    }
}