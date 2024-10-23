package com.paycraft.ems.trip.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.paycraft.R
import com.paycraft.databinding.ActivityCreateTripBinding
import com.paycraft.ems.options_picker.CustomFieldsOptionsListener
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.TransactionField
import com.paycraft.ems.transactions.create.CreateRecordBaseActivity
import com.paycraft.ems.trip.create.CreateTripAdditionalActivity.Companion.E_CAB
import com.paycraft.ems.trip.create.CreateTripAdditionalActivity.Companion.E_FLIGHT
import com.paycraft.ems.trip.create.CreateTripAdditionalActivity.Companion.E_HOTEL
import com.paycraft.ems.trip.create.CreateTripAdditionalActivity.Companion.E_OTHERS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTripActivity : CreateRecordBaseActivity(), View.OnClickListener, CreateTripView,
    CustomFieldsOptionsListener {
    companion object {
        const val E_TRIP_ID = "id";
        const val E_TRIP = "trip";
        const val RC = 103
        fun start(activity: Activity, id: String = "") {
            Intent(activity, CreateTripActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.putExtra(E_TRIP_ID, id)
                }.run {
                    activity.startActivityForResult(this, RC)
                }
        }
    }

    private lateinit var binding: ActivityCreateTripBinding
    private lateinit var viewModel: CreateTripViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_trip)
        viewModel = ViewModelProvider(this)[CreateTripViewModel::class.java]
        viewModel.setView(this)
        configToolbar(
            binding.toolbarLayout.toolbar,
            binding.toolbarLayout.toolbarTitleTv, "Add Trip"
        )
        binding.cancelTv.setOnClickListener(this)
        binding.saveTv.setOnClickListener(this)
        binding.flightReservationTv.setOnClickListener(this)
        binding.hotelReservationTv.setOnClickListener(this)
        binding.cabRentalsTv.setOnClickListener(this)
        binding.othersTv.setOnClickListener(this)

        viewModel.fields.observe(this) {
            it.forEach { f ->
                createField(f)?.let {
                    binding.transactionFieldsLl.addView(it)
                }
            }
        }

        viewModel.getTripFields(intent.getStringExtra(E_TRIP_ID))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK != resultCode || CreateTripAdditionalActivity.RC != requestCode) {
            return
        }
        viewModel.transactionRequest =
            data?.getParcelableExtra(CreateTripAdditionalActivity.E_TRIP_ADDITIONAL_DATA)!!
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.flightReservationTv -> {
                CreateTripAdditionalActivity.start(this, E_FLIGHT, viewModel.transactionRequest)
            }
            R.id.hotelReservationTv -> {
                CreateTripAdditionalActivity.start(this, E_HOTEL, viewModel.transactionRequest)
            }
            R.id.cabRentalsTv -> {
                CreateTripAdditionalActivity.start(this, E_CAB, viewModel.transactionRequest)
            }
            R.id.othersTv -> {
                CreateTripAdditionalActivity.start(this, E_OTHERS, viewModel.transactionRequest)
            }
            R.id.cancelTv -> {
                finish()
            }
            R.id.saveTv -> {
                if (!isCustomFieldsOk(viewModel.fields.value, binding.transactionFieldsLl)) {
                    return
                }
                viewModel.transactionRequest.let {
                    viewModel.transactionRequest.fieldValues = prepareRequest(
                        viewModel.fields.value ?: arrayListOf(),
                        binding.transactionFieldsLl
                    ) ?: arrayListOf()
                    viewModel.createTrip(
                        it,
                        intent.getStringExtra(E_TRIP_ID) ?: ""
                    )
                }
            }
        }
    }

    override fun onCreateTripSuccess(trip: Trip) {
        intent.apply {
            refreshPreviousScreen()
            putExtra(E_REFRESH_PREVIOUS_SCREEN, refreshPreviousScreen)
            setResult(RESULT_OK, intent.putExtra(E_TRIP, trip))
        }.run {
            setResult(RESULT_OK, this)
        }
        finish()
    }

    override fun onOptionSelected(transactionField: TransactionField, fieldOption: FieldOption) {
        binding.transactionFieldsLl.findViewWithTag<EditText>(transactionField.fieldId)
            ?.setText(fieldOption.value ?: "")
        (binding
            .transactionFieldsLl.findViewWithTag<EditText>(transactionField.fieldId)?.parent as ConstraintLayout?)?.findViewById<TextView>(
            R.id.titleTv
        )?.tag = fieldOption.id ?: ""
    }
}