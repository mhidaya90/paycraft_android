package com.paycraft.ems.trip.details

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowStayBinding
import com.paycraft.ems.transactions.HotelReservation

class StayAdapter(
    private val mContext: Context,
    private val mList: List<HotelReservation>
) : RecyclerView.Adapter<StayAdapter.TripJourneyViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripJourneyViewModel {
        val mBinding: RowStayBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.row_stay, parent, false
        )
        return TripJourneyViewModel(mBinding)
    }

    override fun onBindViewHolder(holder: TripJourneyViewModel, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class TripJourneyViewModel(val mBinding: RowStayBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(fieldOption: HotelReservation) {
            mBinding.stay = fieldOption
            mBinding.executePendingBindings()
        }
    }
}