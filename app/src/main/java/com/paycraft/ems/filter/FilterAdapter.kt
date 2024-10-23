package com.paycraft.ems.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowFilterBinding
import com.paycraft.ems.transactions.Filter

class FilterAdapter(
    val filters: MutableList<Filter>,
    val isSelectable: Boolean = true,
    val listener: FilterAdapterListener? = null
) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    inner class ViewHolder(val mBinding: RowFilterBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {
        init {
            mBinding.root.setOnClickListener(this)
        }

        fun bind(filter: Filter) {
            mBinding.filter = filter
            if (filters[layoutPosition].status == "All" || isSelectable)
                mBinding.filterTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            else
                mBinding.filterTv.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_close_filter,
                    0
                )
            mBinding.executePendingBindings()
        }

        override fun onClick(v: View) {
            if (filters[layoutPosition].status == "All") return
            if (isSelectable) {
                filters[layoutPosition].isSelected = !filters[layoutPosition].isSelected
                notifyItemChanged(layoutPosition)
            } else {
                listener?.onRemoveFilter(filters[layoutPosition])
                filters.removeAt(layoutPosition)
                notifyItemRemoved(layoutPosition)
                if (filters.isEmpty())
                    filters.add(Filter("All", "All"))
                notifyItemChanged(0)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowFilterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_filter, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filters[position])
    }

    override fun getItemCount(): Int = filters.size
}

@BindingAdapter("android:stateTextColor")
fun setDisplay(tv: TextView, isSelected: Boolean) {
    tv.setTextColor(
        if (isSelected)
            ContextCompat.getColor(
                tv.context,
                R.color.white
            )
        else
            ContextCompat.getColor(
                tv.context,
                R.color.black
            )
    )
}
