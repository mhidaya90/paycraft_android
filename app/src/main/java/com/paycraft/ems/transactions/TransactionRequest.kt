package com.paycraft.ems.transactions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
open class TransactionRequest(
    @SerializedName("field_values") open var fieldValues: List<FieldOption>?
) : Parcelable

@Parcelize
open class TripRequest(
    @SerializedName("field_values") var fieldValues: List<FieldOption>?,
    @SerializedName("flight_reservations") var flightReservations: FlightReservationData? = null,
    @SerializedName("hotel_reservations") var hotelReservations: List<HotelReservation>? = null,
    @SerializedName("car_rentals") var carRentals: List<CabReservation>? = null,
    @SerializedName("other_bookings") var otherBookings: List<OtherReservation>? = null
) : Parcelable

@Parcelize
class FlightReservationData(
    @SerializedName("trips") val trips: List<Journey>?,
    @SerializedName("time_preference") val timePreference: String?,
    @SerializedName("seat_preference") val seatPreference: String?,
    @SerializedName("meal_preference") val mealPreference: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("mode") val travelMode: String?,
) : Parcelable

@Parcelize
class Journey(
    @SerializedName("departure_date") val depatureDate: String?,
    @SerializedName("return_date") val returnDate: String?,
    @SerializedName("from_city") val fromCity: String?,
    @SerializedName("to_city") val toCity: String?
) : Parcelable

@Parcelize
class HotelReservation(
    @SerializedName("location") val location: String?,
    @SerializedName("duration_from") val durationFrom: String?,
    @SerializedName("duration_to") val durationTo: String?,
    @SerializedName("description") val description: String?
) : Parcelable

@Parcelize
class CabReservation(
    @SerializedName("duration_from") val durationFrom: String?,
    @SerializedName("duration_to") val durationTo: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("description") val description: String?
) : Parcelable

@Parcelize
class OtherReservation(
    @SerializedName("departure_date") val departureDate: String?,
    @SerializedName("from_city") val fromCity: String?,
    @SerializedName("to_city") val toCity: String?,
    @SerializedName("travel_mode") val travelMode: String?,
    @SerializedName("description") val description: String?
) : Parcelable
