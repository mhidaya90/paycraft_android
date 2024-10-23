package com.paycraft.ems.advance.create

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
import com.paycraft.databinding.ActivityCreateAdvanceBinding
import com.paycraft.ems.advance.list.Advance
import com.paycraft.ems.options_picker.CustomFieldsOptionsListener
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.TransactionField
import com.paycraft.ems.transactions.create.CreateRecordBaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAdvanceActivity : CreateRecordBaseActivity(), View.OnClickListener, CreateAdvanceView,
    CustomFieldsOptionsListener {
    companion object {
        const val E_ADVANCE_ID = "id";
        const val E_ADVANCE = "advance";
        const val RC = 102
        fun start(activity: Activity, id: String = "") {
            Intent(activity, CreateAdvanceActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.putExtra(E_ADVANCE_ID, id)
                }.run {
                    activity.startActivityForResult(this, RC)
                }
        }
    }

    lateinit var viewModel: CreateAdvanceViewModel
    lateinit var binding: ActivityCreateAdvanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_advance)
        configToolbar(
            binding.toolbarLayout.toolbar,
            binding.toolbarLayout.toolbarTitleTv, "Add Advance"
        )
        binding.cancelTv.setOnClickListener(this)
        binding.saveTv.setOnClickListener(this)
        viewModel =
            ViewModelProvider(this).get(CreateAdvanceViewModel::class.java)
        viewModel.setView(this)

        viewModel.fields.observe(this) {
            it.forEach { f ->
                createField(f)?.let {
                    binding.transactionFieldsLl.addView(it)
                }
            }
        }
        viewModel.getAdvanceFields(intent.getStringExtra(E_ADVANCE_ID) ?: "")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancelTv -> {
                finish()
            }

            R.id.saveTv -> {
                if (!isCustomFieldsOk(viewModel.fields.value, binding.transactionFieldsLl)) {
                    return
                }
                viewModel.createAdvance(
                    prepareRequest(
                        viewModel.fields.value ?: arrayListOf(),
                        binding.transactionFieldsLl
                    ) ?: arrayListOf(),
                    intent.getStringExtra(E_ADVANCE_ID) ?: ""
                )
            }
        }
    }

    override fun onCreateAdvanceSuccess(advance: Advance) {
        intent.apply {
            refreshPreviousScreen()
            putExtra(E_REFRESH_PREVIOUS_SCREEN, refreshPreviousScreen)
            setResult(RESULT_OK, intent.putExtra(E_ADVANCE, advance))
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