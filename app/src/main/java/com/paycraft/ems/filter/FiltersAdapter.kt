package com.paycraft.ems.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowFiltersBinding
import com.paycraft.ems.transactions.Filter
import com.paycraft.ems.transactions.Filters

class FiltersAdapter(
    val filters: List<Filters>
) : RecyclerView.Adapter<FiltersAdapter.ViewHolder>() {

    inner class ViewHolder(val mBinding: RowFiltersBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(filter: Filters) {
            mBinding.filters = filter
            mBinding.filtersRv.adapter = SelectableFilterAdapter((filter.filters ?: arrayListOf()) as MutableList<Filter>)
            mBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowFiltersBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_filters, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filters[position])
    }

    override fun getItemCount(): Int = filters.size
}