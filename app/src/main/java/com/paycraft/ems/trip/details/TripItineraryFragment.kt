package com.paycraft.ems.trip.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.paycraft.R
import com.paycraft.base.BaseFragment
import com.paycraft.databinding.FragmentTripItineraryBinding
import com.paycraft.ems.transactions.Journey
import com.paycraft.ems.trip.create.Trip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripItineraryFragment : BaseFragment() {
    companion object {
        const val TAG = "Trip Itinerary"
        const val E_TRIP = "Trip"
        fun newInstance(
            trip: Trip
        ): TripItineraryFragment {
            val args = Bundle()
            args.putParcelable(E_TRIP, trip)
            val fragment = TripItineraryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var mBinding: FragmentTripItineraryBinding
    private var mTrip: Trip? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_trip_itinerary, container, false)
        mTrip = arguments?.getParcelable(E_TRIP)
        mTrip?.flightReservations?.trips?.let {
            mBinding.flightReservationTextTv.visibility = if (it.isEmpty()) GONE else VISIBLE
            mBinding.flightReservationRv.visibility = if (it.isEmpty()) GONE else VISIBLE
            if ("round_trip" == (mTrip?.flightReservations?.travelMode ?: "")) {
                val list = mutableListOf<Journey>()
                if (it.isNotEmpty()) {
                    it.firstOrNull()?.let { f ->
                        list.add(f)
                        list.add(
                            Journey(
                                f.returnDate,
                                f.depatureDate,
                                f.toCity,
                                f.fromCity
                            )
                        )
                    }
                }
                mBinding.flightReservationRv.adapter = TripJourneyAdapter(requireContext(), list)

            } else {
                mBinding.flightReservationRv.adapter = TripJourneyAdapter(requireContext(), it)
            }
        }
        mTrip?.hotelReservations?.let {
            mBinding.hotelReservationTextTv.visibility = if (it.isEmpty()) GONE else VISIBLE
            mBinding.hotelReservationRv.visibility = if (it.isEmpty()) GONE else VISIBLE
            mBinding.hotelReservationRv.adapter = StayAdapter(requireContext(), it)
        }
        mTrip?.carRentals?.map { Journey(it.city, "", it.durationFrom, it.durationTo) }?.let {
            mBinding.cabReservationTextTv.visibility = if (it.isEmpty()) GONE else VISIBLE
            mBinding.carReservationRv.visibility = if (it.isEmpty()) GONE else VISIBLE
            mBinding.carReservationRv.adapter = TripJourneyAdapter(requireContext(), it)
        }
        mTrip?.otherBookings?.map {
            Journey(
                "${it.departureDate} (${it.travelMode})",
                "",
                it.fromCity,
                it.toCity
            )
        }?.let {
            mBinding.otherReservationTextTv.visibility = if (it.isEmpty()) GONE else VISIBLE
            mBinding.otherReservationRv.visibility = if (it.isEmpty()) GONE else VISIBLE
            mBinding.otherReservationRv.adapter = TripJourneyAdapter(requireContext(), it)
        }
        return mBinding.root
    }
}