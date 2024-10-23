package com.paycraft.ems.trip.details

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowJourneyBinding
import com.paycraft.ems.transactions.Journey

class TripJourneyAdapter(
    private val mContext: Context,
    private val mList: List<Journey>
) : RecyclerView.Adapter<TripJourneyAdapter.TripJourneyViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripJourneyViewModel {
        val mBinding: RowJourneyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.row_journey, parent, false
        )
        return TripJourneyViewModel(mBinding)
    }

    override fun onBindViewHolder(holder: TripJourneyViewModel, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class TripJourneyViewModel(val mBinding: RowJourneyBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(fieldOption: Journey) {
            mBinding.journey = fieldOption
            mBinding.executePendingBindings()
        }
    }
}