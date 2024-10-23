package com.paycraft.ems.reports

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowPolicyViolationsBinding
import com.paycraft.ems.reports.detail.DisplayViolation

class PolicyLimitsAdapter(
    private val mContext: Context,
    private val mLimits: List<DisplayViolation>
) :
    RecyclerView.Adapter<PolicyLimitsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val binding: RowPolicyViolationsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.row_policy_violations, viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.bind(mLimits[i])
    }

    override fun getItemCount(): Int = mLimits.size

    inner class ViewHolder(val binding: RowPolicyViolationsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(displayViolation: DisplayViolation) {
            binding.v = displayViolation
            binding.countTv.text = "${layoutPosition + 1}. "
            binding.executePendingBindings()
        }
    }
}

@BindingAdapter("android:violationTextColor")
fun violationTextColor(tv: TextView, isError: Boolean) {
    tv.setTextColor(
        if (isError)
            ContextCompat.getColor(
                tv.context,
                R.color.colorRed
            )
        else
            ContextCompat.getColor(
                tv.context,
                R.color.orange
            )
    )
}
