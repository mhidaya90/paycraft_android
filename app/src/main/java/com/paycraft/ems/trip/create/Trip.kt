package com.paycraft.ems.trip.create

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.paycraft.ems.Record
import com.paycraft.ems.reports.Report
import com.paycraft.ems.transactions.*
import kotlinx.android.parcel.Parcelize

@Parcelize
class Trip(
    @SerializedName("title") val title: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("trip_type") val tripType: String?,
    @SerializedName("is_visa_required") val isVisaRequired: String?,
    @SerializedName("field_values") var fieldValues: List<FieldOption>?
) : Record(), Parcelable {
    val displayDate: String? = null

    @SerializedName("flight_reservations")
    var flightReservations: FlightReservationData? = null

    @SerializedName("hotel_reservations")
    var hotelReservations: List<HotelReservation>? = null

    @SerializedName("car_rentals")
    var carRentals: List<CabReservation>? = null

    @SerializedName("other_bookings")
    var otherBookings: List<OtherReservation>? = null

    @SerializedName("report")
    var report: List<Report>? = null
}

