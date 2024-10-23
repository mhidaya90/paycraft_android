package com.paycraft.ems.trip.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.TimeUtil
import com.paycraft.base.toast
import com.paycraft.databinding.ActivityCreateTripAdditionalBinding
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.options_picker.OptionsListener
import com.paycraft.ems.transactions.CabReservation
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.FlightReservationData
import com.paycraft.ems.transactions.HotelReservation
import com.paycraft.ems.transactions.Journey
import com.paycraft.ems.transactions.OtherReservation
import com.paycraft.ems.transactions.TripRequest
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
@AndroidEntryPoint
class CreateTripAdditionalActivity : BaseActivity(), View.OnClickListener, OptionsListener {
    companion object {
        const val E_FLIGHT = "flight";
        const val E_HOTEL = "hotel";
        const val E_CAB = "cab";
        const val E_OTHERS = "others";

        const val E_TRIP_ADDITIONAL_TYPE = "trip_additional_details";
        const val E_TRIP_ADDITIONAL_DATA = "trip_additional_data";
        const val RC = 105

        fun start(activity: Activity, type: String, transactionRequest: TripRequest?) {
            Intent(activity, CreateTripAdditionalActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.putExtra(E_TRIP_ADDITIONAL_TYPE, type)
                    transactionRequest?.let {
                        this.putExtra(E_TRIP_ADDITIONAL_DATA, transactionRequest)
                    }
                }.run {
                    activity.startActivityForResult(this, RC)
                }
        }
    }

    var tempCal = Calendar.getInstance()
    private var mEditText: EditText? = null
    private var mType: String = ""
    private var mTripAdditionalData: TripRequest? = null
    lateinit var mBinding: ActivityCreateTripAdditionalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_trip_additional)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, "Add Trip"
        )

        mBinding.addNextCityTv.setOnClickListener(this)
        mBinding.addHotelTv.setOnClickListener(this)
        mBinding.addCarTv.setOnClickListener(this)
        mBinding.addOtherTv.setOnClickListener(this)
        mBinding.editTv.setOnClickListener(this)
        mBinding.submitTv.setOnClickListener(this)
        mBinding.timePreferenceEt.setOnClickListener(this)
        mBinding.mealPreferenceEt.setOnClickListener(this)
        mBinding.seatPreferenceEt.setOnClickListener(this)

        mType = intent.getStringExtra(E_TRIP_ADDITIONAL_TYPE) ?: ""
        if (intent.hasExtra(E_TRIP_ADDITIONAL_TYPE))
            mTripAdditionalData = intent.getParcelableExtra(E_TRIP_ADDITIONAL_DATA)

        mBinding.tripTypeTg.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.oneWayRb -> {
                    mBinding.addNextCityTv.visibility = GONE
                    mBinding.multiCityLl.removeAllViews()
                    val v = createField(mBinding.multiCityLl, R.layout.layout_flight)
                }

                R.id.roundTripRb -> {
                    mBinding.addNextCityTv.visibility = GONE
                    mBinding.multiCityLl.removeAllViews()
                    val v = createField(mBinding.multiCityLl, R.layout.layout_flight)
                    v.findViewById<View>(R.id.upAndDownIv).visibility = VISIBLE
                    v.findViewById<View>(R.id.returnDateEt).visibility = VISIBLE
                    v.findViewById<View>(R.id.returnDateTv).visibility = VISIBLE
                }

                R.id.multiCityRb -> {
                    mBinding.addNextCityTv.visibility = VISIBLE
                    mBinding.multiCityLl.removeAllViews()
                    createField(mBinding.multiCityLl, R.layout.layout_flight)
                }
            }
        }
        prepareUi()
        populateData()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addNextCityTv -> {
                createField(mBinding.multiCityLl, R.layout.layout_flight)
            }

            R.id.seatPreferenceEt -> {
                mEditText = v as EditText
                val options = arrayListOf(
                    FieldOption("any", "Any"),
                    FieldOption(
                        "window",
                        "Window"
                    ),
                    FieldOption(
                        "middle",
                        "Middle"
                    ),
                    FieldOption("aisle", "Aisle"),
                )
                OptionsBottomSheetFragment.start(
                    supportFragmentManager,
                    options = options
                )
            }

            R.id.mealPreferenceEt -> {
                mEditText = v as EditText
                val options = arrayListOf(
                    FieldOption("none", "None"),
                    FieldOption(
                        "vegetarian",
                        "Vegetarian"
                    ),
                    FieldOption(
                        "non_vegetarian",
                        "Non - Vegetarian"
                    ),
                )
                OptionsBottomSheetFragment.start(
                    supportFragmentManager,
                    options = options
                )
            }

            R.id.timePreferenceEt -> {
                mEditText = v as EditText
                val options = arrayListOf(
                    FieldOption("morning", "Morning (Before 11 AM)"),
                    FieldOption("afternoon", "Afternoon (11 AM - 5 PM)"),
                    FieldOption("evening", "Evening (5 PM - 9 PM)"),
                    FieldOption("night", "Night (After 9 PM)"),
                )
                OptionsBottomSheetFragment.start(supportFragmentManager, options = options)
            }

            R.id.editTv -> {
                finish()
            }

            R.id.submitTv -> {
                val validationError = packData()
                if (validationError.isNotEmpty()) {
                    this toast validationError
                    return
                }
                val i = Intent()
                i.putExtra(E_TRIP_ADDITIONAL_DATA, mTripAdditionalData)
                setResult(RESULT_OK, i)
                finish()
            }

            R.id.addHotelTv -> {
                createField(mBinding.hotelLl, R.layout.layout_hotel)
            }

            R.id.addCarTv -> {
                createField(mBinding.cabLl, R.layout.layout_cab)
            }

            R.id.addOtherTv -> {
                createField(mBinding.othersLl, R.layout.layout_other_travels)
            }
        }
    }

    override fun onOptionSelected(type: String, fieldOption: FieldOption) {
        mEditText?.setText(fieldOption.value)
        mEditText?.tag = fieldOption.id
    }

    private fun createField(rootLayout: LinearLayout, layout: Int): View {
        val v = layoutInflater.inflate(layout, null)
        rootLayout.addView(buildTripItem(layout, v))
        return v
    }

    private fun buildTripItem(layout: Int, v: View): View {
        return when (layout) {
            R.layout.layout_flight -> {
                flightView(v)
            }

            R.layout.layout_hotel -> {
                hotelVIew(v)
            }

            R.layout.layout_cab -> {
                cabView(v)
            }

            else -> {
                otherTravelView(v)
            }
        }
    }

    private fun flightView(v: View): View {
        v.findViewById<EditText>(R.id.departureDateEt).setOnClickListener {
            customFieldDateTimeDialog(it as EditText, true, true)
        }
        v.findViewById<EditText>(R.id.returnDateEt).setOnClickListener {
            customFieldDateTimeDialog(it as EditText, true, true)
        }
        return v
    }

    private fun hotelVIew(v: View): View {
        v.findViewById<EditText>(R.id.durationFromEt).setOnClickListener {
            customFieldDateTimeDialog(it as EditText, true, true)
        }
        v.findViewById<EditText>(R.id.durationToEt).setOnClickListener {
            customFieldDateTimeDialog(it as EditText, true, true)
        }
        return v
    }

    private fun cabView(v: View): View {
        v.findViewById<EditText>(R.id.carDurationFromEt).setOnClickListener {
            customFieldDateTimeDialog(it as EditText, true, true)
        }
        v.findViewById<EditText>(R.id.carDurationToEt).setOnClickListener {
            customFieldDateTimeDialog(it as EditText, true, true)
        }
        return v
    }

    private fun otherTravelView(v: View): View {
        v.findViewById<EditText>(R.id.depatureDateEt).setOnClickListener {
            customFieldDateTimeDialog(it as EditText, true, false)
        }
        v.findViewById<EditText>(R.id.travelModeEt).let {
            mEditText = it
            it.setOnClickListener {
                val options = arrayListOf(
                    FieldOption("bus", "Bus"),
                    FieldOption("train", "Train"),
                    FieldOption("others", "Others"),
                )
                OptionsBottomSheetFragment.start(supportFragmentManager, options = options)
            }
        }

        return v
    }

    private fun prepareUi() {
        when (mType) {
            E_FLIGHT -> {
                mBinding.flightReservationLl.visibility = VISIBLE
                mBinding.hotelLl.visibility = GONE
                mBinding.cabLl.visibility = GONE
                mBinding.othersLl.visibility = GONE
                mBinding.tripTypeTg.check(R.id.oneWayRb)
            }

            E_HOTEL -> {
                mBinding.flightReservationLl.visibility = GONE
                mBinding.hotelLl.visibility = VISIBLE
                mBinding.cabLl.visibility = GONE
                mBinding.othersLl.visibility = GONE

                mBinding.addHotelTv.visibility = VISIBLE
                mBinding.addCarTv.visibility = GONE
                mBinding.addOtherTv.visibility = GONE
                createField(mBinding.hotelLl, R.layout.layout_hotel)
            }

            E_CAB -> {
                mBinding.flightReservationLl.visibility = GONE
                mBinding.hotelLl.visibility = GONE
                mBinding.cabLl.visibility = VISIBLE
                mBinding.othersLl.visibility = GONE

                mBinding.addHotelTv.visibility = GONE
                mBinding.addCarTv.visibility = VISIBLE
                mBinding.addOtherTv.visibility = GONE
                createField(mBinding.cabLl, R.layout.layout_cab)
            }

            E_OTHERS -> {
                mBinding.flightReservationLl.visibility = GONE
                mBinding.hotelLl.visibility = GONE
                mBinding.cabLl.visibility = GONE
                mBinding.othersLl.visibility = VISIBLE

                mBinding.addHotelTv.visibility = GONE
                mBinding.addCarTv.visibility = GONE
                mBinding.addOtherTv.visibility = VISIBLE
                createField(mBinding.othersLl, R.layout.layout_other_travels)
            }

            else -> {
                finish()
            }
        }
    }

    private fun populateData() {
        mTripAdditionalData?.let {
            when (mType) {
                E_FLIGHT -> {
                    when (mTripAdditionalData?.flightReservations?.travelMode) {
                        "round_trip" -> {
                            mBinding.roundTripRb.isChecked = true

                        }

                        "multi_city" -> {
                            mBinding.multiCityRb.isChecked = true
                        }

                        else -> {
                            mBinding.oneWayRb.isChecked = true
                        }
                    }
                    mTripAdditionalData?.flightReservations?.let {
                        mBinding.timePreferenceEt.setText(it.timePreference)
                        mBinding.mealPreferenceEt.setText(it.mealPreference)
                        mBinding.seatPreferenceEt.setText(it.seatPreference)
                        mBinding.descriptionEt.setText(it.description)
                        it.trips?.let { trips ->
                            if (trips.isNotEmpty()) mBinding.multiCityLl.removeAllViews()
                            for (i in trips) {
                                val v = createField(mBinding.multiCityLl, R.layout.layout_flight)
                                v.findViewById<EditText>(R.id.departureDateEt)
                                    .setText(i.depatureDate)
                                v.findViewById<EditText>(R.id.returnDateEt).setText(i.returnDate)
                                v.findViewById<EditText>(R.id.fromCityEt).setText(i.fromCity)
                                v.findViewById<EditText>(R.id.toCityEt).setText(i.toCity)

                                if ("round_trip" == mTripAdditionalData?.flightReservations?.travelMode) {
                                    v.findViewById<TextView>(R.id.returnDateTv).visibility = VISIBLE
                                    v.findViewById<EditText>(R.id.returnDateEt).visibility = VISIBLE
                                    v.findViewById<View>(R.id.returnDateView).visibility = VISIBLE
                                }
                            }
                        }

                    }
                }

                E_HOTEL -> {
                    mTripAdditionalData?.hotelReservations?.let {
                        if (it.isNotEmpty()) mBinding.hotelLl.removeAllViews()
                        for (i in it) {
                            val v = createField(mBinding.hotelLl, R.layout.layout_hotel)
                            v.findViewById<EditText>(R.id.locationEt).setText(i.location)
                            v.findViewById<EditText>(R.id.durationFromEt).setText(i.durationFrom)
                            v.findViewById<EditText>(R.id.durationToEt).setText(i.durationTo)
                            v.findViewById<EditText>(R.id.hotelDescriptionEt).setText(i.description)
                        }
                    }
                }

                E_CAB -> {
                    mTripAdditionalData?.carRentals?.let {
                        if (it.isNotEmpty()) mBinding.cabLl.removeAllViews()
                        for (i in it) {
                            val v = createField(mBinding.cabLl, R.layout.layout_cab)
                            v.findViewById<EditText>(R.id.carDurationFromEt).setText(i.durationFrom)
                            v.findViewById<EditText>(R.id.carDurationToEt).setText(i.durationTo)
                            v.findViewById<EditText>(R.id.cityEt).setText(i.city)
                            v.findViewById<EditText>(R.id.carDescriptionToEt).setText(i.description)
                        }
                    }
                }

                E_OTHERS -> {
                    mTripAdditionalData?.otherBookings?.let {
                        if (it.isNotEmpty()) mBinding.othersLl.removeAllViews()
                        for (i in it) {
                            val v = createField(mBinding.othersLl, R.layout.layout_other_travels)
                            v.findViewById<EditText>(R.id.depatureDateEt).setText(i.departureDate)
                            v.findViewById<EditText>(R.id.othersFromCityEt).setText(i.fromCity)
                            v.findViewById<EditText>(R.id.othersToCityEt).setText(i.toCity)
                            v.findViewById<EditText>(R.id.travelModeEt).setText(i.travelMode)
                            v.findViewById<EditText>(R.id.otherDescriptionModeEt)
                                .setText(i.description)
                        }
                    }
                }

                else -> {
                    finish()
                }
            }
        }
    }

    private fun packData(): String {
        if (null == mTripAdditionalData)
            mTripAdditionalData = TripRequest(null)
        when (mType) {
            E_FLIGHT -> {
                val tripType = when (mBinding.tripTypeTg.checkedRadioButtonId) {
                    R.id.roundTripRb -> "round_trip"
                    R.id.multiCityRb -> "multi_city"
                    else -> "one_way"
                }
                val trips = arrayListOf<Journey>()
                for (v in 0 until findViewById<LinearLayout>(R.id.multiCityLl).childCount) {
                    val multiCityLl = findViewById<LinearLayout>(R.id.multiCityLl).getChildAt(v)
                    val departureDate =
                        multiCityLl.findViewById<EditText>(R.id.departureDateEt).text.toString()
                    val returnDate =
                        multiCityLl.findViewById<EditText>(R.id.returnDateEt).text.toString()
                    val fromCity =
                        multiCityLl.findViewById<EditText>(R.id.fromCityEt).text.toString()
                    val toCity = multiCityLl.findViewById<EditText>(R.id.toCityEt).text.toString()

                    if (departureDate.isEmpty()) {
                        return "Departure Date is empty!"
                    }
                    if (fromCity.isEmpty()) {
                        return "From city is empty!"
                    }
                    if (toCity.isEmpty()) {
                        return "To city is empty!"
                    }
                    if ("round_trip" == tripType && returnDate.isEmpty()) {
                        return "Return Date is empty!"
                    }
                    trips.add(
                        Journey(
                            departureDate,
                            returnDate,
                            fromCity,
                            toCity
                        )
                    )
                }

                val timePreference = findViewById<EditText>(R.id.timePreferenceEt).text.toString()
                val seatPreference = findViewById<EditText>(R.id.seatPreferenceEt).text.toString()
                val mealPreference = findViewById<EditText>(R.id.mealPreferenceEt).text.toString()
                val description = findViewById<EditText>(R.id.descriptionEt).text.toString()

                if (timePreference.isEmpty()) {
                    return "Time Preference is empty!"
                }
                if (seatPreference.isEmpty()) {
                    return "Seat Preference is empty!"
                }
                if (mealPreference.isEmpty()) {
                    return "Meal Preference is empty!"
                }
                if (description.isEmpty()) {
                    return "Description is empty!"
                }

                val flightData = FlightReservationData(
                    trips,
                    timePreference,
                    seatPreference,
                    mealPreference,
                    description,
                    tripType
                )
                mTripAdditionalData?.flightReservations = flightData
            }

            E_HOTEL -> {
                val hotelData = arrayListOf<HotelReservation>()
                for (v in 0 until findViewById<LinearLayout>(R.id.hotelLl).childCount) {
                    val hotel = findViewById<LinearLayout>(R.id.hotelLl).getChildAt(v)
                    val location = hotel.findViewById<EditText>(R.id.locationEt).text.toString()
                    val durationFrom =
                        hotel.findViewById<EditText>(R.id.durationFromEt).text.toString()
                    val durationTo =
                        hotel.findViewById<EditText>(R.id.durationToEt).text.toString()
                    val hotelDescription =
                        hotel.findViewById<EditText>(R.id.hotelDescriptionEt).text.toString()

                    if (location.isEmpty()) {
                        return "Location is empty!"
                    }
                    if (durationFrom.isEmpty()) {
                        return "From duration is empty!"
                    }
                    if (durationTo.isEmpty()) {
                        return "To Duration is empty!"
                    }
                    if (hotelDescription.isEmpty()) {
                        return "Hotel is empty!"
                    }

                    hotelData.add(
                        HotelReservation(
                            location,
                            durationFrom,
                            durationTo,
                            hotelDescription,
                        )
                    )
                }
                mTripAdditionalData?.hotelReservations = hotelData
            }

            E_CAB -> {
                val cabData = arrayListOf<CabReservation>()
                for (v in 0 until findViewById<LinearLayout>(R.id.cabLl).childCount) {
                    val cab = findViewById<LinearLayout>(R.id.cabLl).getChildAt(v)

                    val durationFrom =
                        cab.findViewById<EditText>(R.id.carDurationFromEt).text.toString()
                    val durationTo =
                        cab.findViewById<EditText>(R.id.carDurationToEt).text.toString()
                    val city =
                        cab.findViewById<EditText>(R.id.cityEt).text.toString()
                    val description =
                        cab.findViewById<EditText>(R.id.carDescriptionToEt).text.toString()

                    if (durationFrom.isEmpty()) {
                        return "From duration is empty!"
                    }
                    if (durationTo.isEmpty()) {
                        return "To Duration is empty!"
                    }
                    if (city.isEmpty()) {
                        return "City is empty!"
                    }
                    if (description.isEmpty()) {
                        return "Description is empty!"
                    }
                    cabData.add(
                        CabReservation(
                            durationFrom,
                            durationTo,
                            city,
                            description,
                        )
                    )
                }
                mTripAdditionalData?.carRentals = cabData
            }

            E_OTHERS -> {
                val others = arrayListOf<OtherReservation>()
                for (v in 0 until findViewById<LinearLayout>(R.id.othersLl).childCount) {
                    val other = findViewById<LinearLayout>(R.id.othersLl).getChildAt(v)
                    val departureDate =
                        other.findViewById<EditText>(R.id.depatureDateEt).text.toString()
                    val othersFromCity =
                        other.findViewById<EditText>(R.id.othersFromCityEt).text.toString()
                    val othersToCity =
                        other.findViewById<EditText>(R.id.othersToCityEt).text.toString()
                    val travelMode =
                        other.findViewById<EditText>(R.id.travelModeEt).text.toString()
                    val otherDescription =
                        other.findViewById<EditText>(R.id.otherDescriptionModeEt).text.toString()

                    if (departureDate.isEmpty()) {
                        return "Departure Date is empty!"
                    }
                    if (othersFromCity.isEmpty()) {
                        return "From City is empty!"
                    }
                    if (othersToCity.isEmpty()) {
                        return "To City is empty!"
                    }
                    if (travelMode.isEmpty()) {
                        return "Travel Mode is empty!"
                    }
                    if (otherDescription.isEmpty()) {
                        return "Description is empty!"
                    }
                    others.add(
                        OtherReservation(
                            departureDate,
                            othersFromCity,
                            othersToCity,
                            travelMode,
                            otherDescription
                        )
                    )
                }
                mTripAdditionalData?.otherBookings = others
            }

            else -> {
                finish()
            }
        }
        return ""
    }

    private fun customFieldDateTimeDialog(
        editTex: EditText,
        isDateEnabled: Boolean,
        isTimeEnabled: Boolean
    ) {
        val dialog = customDialog(R.layout.dialog_date_time_picker)
        val datePicker = dialog.findViewById<DatePicker>(R.id.date_picker)
        val timePicker = dialog.findViewById<TimePicker>(R.id.time_picker)
        if (isDateEnabled) {
            datePicker.visibility = VISIBLE
        } else {
            datePicker.visibility = GONE
        }
        if (isTimeEnabled) {
            (dialog.findViewById<View>(R.id.tv_dialog_title) as TextView).text =
                "Select Date and Time"
            timePicker.visibility = VISIBLE
        } else {
            (dialog.findViewById<View>(R.id.tv_dialog_title) as TextView).text = "Select Date"
            timePicker.visibility = GONE
        }
        dialog.findViewById<View>(R.id.ll_dialog_close)
            .setOnClickListener { v: View? -> dialog.dismiss() }
        dialog.findViewById<View>(R.id.tv_continue).setOnClickListener { view: View? ->
            tempCal.set(Calendar.YEAR, datePicker.year)
            tempCal.set(Calendar.MONTH, datePicker.month)
            tempCal.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
            if (isTimeEnabled) {
                tempCal.set(Calendar.HOUR_OF_DAY, timePicker.currentHour)
                tempCal.set(Calendar.MINUTE, timePicker.currentMinute)
            } else {
                tempCal.set(Calendar.HOUR_OF_DAY, 0)
                tempCal.set(Calendar.MINUTE, 0)
            }
            tempCal.set(Calendar.SECOND, 0)
            tempCal.set(Calendar.MILLISECOND, 0)
            editTex.setText(
                if (isDateEnabled && isTimeEnabled)
                    TimeUtil.millisToDateFormat(
                        tempCal.timeInMillis,
                        TimeUtil.EXPENSE_DISPLAY_DATE_FORMAT
                    )
                else
                    TimeUtil.millisToDateFormat(
                        tempCal.timeInMillis,
                        TimeUtil.EXPENSE_DISPLAY_ONLY_DATE_FORMAT
                    )
            )
            dialog.dismiss()
        }
        dialog.show()
    }
}