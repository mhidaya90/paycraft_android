package com.paycraft.ems.trip.details

import com.paycraft.base.BaseView
import com.paycraft.ems.trip.create.Trip

interface TripView : BaseView {
    fun onTripSuccess(trip: Trip)
    fun onTripSubmittedSuccess()
    fun onTripLinkingSuccess()
    fun onDeleteTripSuccess()
    fun onCreateTripCommentSuccess()
    fun onRecallTripSuccess()
    fun cancelTripSuccess()
    fun closeTripSuccess()
}