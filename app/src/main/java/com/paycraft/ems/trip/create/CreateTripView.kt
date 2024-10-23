package com.paycraft.ems.trip.create

import com.paycraft.base.BaseView

interface CreateTripView : BaseView {
    fun onCreateTripSuccess(trip: Trip)
}