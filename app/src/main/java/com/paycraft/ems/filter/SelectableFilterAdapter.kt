package com.paycraft.ems.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowSelectableFilterBinding
import com.paycraft.ems.transactions.Filter

class SelectableFilterAdapter(
    val filters: MutableList<Filter>,
    val listener: FilterAdapterListener? = null
) : RecyclerView.Adapter<SelectableFilterAdapter.ViewHolder>() {

    inner class ViewHolder(val mBinding: RowSelectableFilterBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {
        init {
            mBinding.root.setOnClickListener(this)
        }

        fun bind(filter: Filter) {
            mBinding.filter = filter
            mBinding.filterTv.isChecked = filter.isSelected
            mBinding.executePendingBindings()
        }

        override fun onClick(v: View) {
            filters[layoutPosition].isSelected = !filters[layoutPosition].isSelected
            notifyItemChanged(layoutPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowSelectableFilterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_selectable_filter, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filters[position])
    }

    override fun getItemCount(): Int = filters.size
}