package com.paycraft.ems.transactions.create

import android.text.InputType
import android.view.View
import android.view.View.GONE
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.Validator
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment.Companion.TYPE_CUSTOM_FILED
import com.paycraft.ems.transactions.COLUMN_TYPE_CHECK_BOX
import com.paycraft.ems.transactions.COLUMN_TYPE_DATE
import com.paycraft.ems.transactions.COLUMN_TYPE_DATE_TIME
import com.paycraft.ems.transactions.COLUMN_TYPE_DROP_DOWN
import com.paycraft.ems.transactions.COLUMN_TYPE_INTEGER
import com.paycraft.ems.transactions.COLUMN_TYPE_STRING
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.TransactionField
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class CreateRecordBaseActivity : BaseActivity() {

    fun createField(transactionField: TransactionField): View? {
        var v: View? = null
        when (transactionField.fieldType) {
            COLUMN_TYPE_DATE -> {
                val e = createEditTextField(transactionField)
                v = e.first
                e.third.apply {
                    hint = "Select " + transactionField.displayName
                    isFocusable = false
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0)
                    setOnClickListener {
                        dateTimeDialog(
                            e.third, isDateEnabled = true, isTimeEnabled = false
                        )
                    }
                    addTextChangedListener {
                        e.first.findViewById<TextView>(R.id.errorInfoTv).visibility = GONE
                    }
                }
            }

            COLUMN_TYPE_DATE_TIME -> {
                val e = createEditTextField(transactionField)
                v = e.first
                e.third.apply {
                    hint = "Select " + transactionField.displayName
                    isFocusable = false
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0)
                    setOnClickListener {
                        dateTimeDialog(
                            e.third, isDateEnabled = true, isTimeEnabled = true
                        )
                    }
                    addTextChangedListener {
                        e.first.findViewById<TextView>(R.id.errorInfoTv).visibility = GONE
                    }
                }
            }

            COLUMN_TYPE_STRING -> {
                val e = createEditTextField(transactionField)
                v = e.first
                e.third.apply {
                    hint = "Enter " + transactionField.displayName
                    inputType = InputType.TYPE_CLASS_TEXT
                    addTextChangedListener {
                        e.first.findViewById<TextView>(R.id.errorInfoTv).visibility = GONE
                    }
                }
            }

            COLUMN_TYPE_INTEGER -> {
                val e = createEditTextField(transactionField)
                v = e.first
                e.third.apply {
                    hint = "Enter " + transactionField.displayName
                    inputType = InputType.TYPE_CLASS_NUMBER
                    addTextChangedListener {
                        e.first.findViewById<TextView>(R.id.errorInfoTv).visibility = GONE
                    }
                }
            }

            COLUMN_TYPE_DROP_DOWN -> {
                val e = createEditTextField(transactionField)
                v = e.first
                e.third.apply {
                    hint = "Select " + transactionField.displayName
                    isFocusable = false
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0)
                    setOnClickListener(CustomFieldClickListener(transactionField))
                    addTextChangedListener {
                        e.first.findViewById<TextView>(R.id.errorInfoTv).visibility = GONE
                    }
                }
            }

            COLUMN_TYPE_CHECK_BOX -> {
                v = layoutInflater.inflate(R.layout.layout_expense_check_box, null)
                val t = v.findViewById<TextView>(R.id.expenseCbIntoTv)
                t.text = transactionField.displayName
                val cb = v.findViewById<CheckBox>(R.id.expenseCb)
                cb.tag = transactionField.fieldId
                cb.isChecked = ("true" == transactionField.selected?.value)
            }
        }
        return v
    }

    fun isCustomFieldsOk(fields: List<TransactionField>?, view: View): Boolean {
        fields?.forEach { i ->
            when (i.fieldType) {
                COLUMN_TYPE_DATE_TIME, COLUMN_TYPE_DATE, COLUMN_TYPE_INTEGER, COLUMN_TYPE_STRING, COLUMN_TYPE_DROP_DOWN -> {
                    val fieldName: String = i.fieldId ?: ""
                    val e: EditText = view.findViewWithTag(fieldName)
                    val ei: TextView = view.findViewWithTag(fieldName + "_i")
                    if (i.isRequired == true && Validator.isNullOrEmpty(e.text.toString())) {
                        ei.let {
                            it.visibility = View.VISIBLE
                            it.text = "Please enter " + i.displayName
                        }
                        return false
                    }
                }

                COLUMN_TYPE_CHECK_BOX -> {
                    return true
                }
            }
        }
        return true
    }

    private fun createEditTextField(transactionField: TransactionField): Triple<View, TextView, EditText> {
        val v = layoutInflater.inflate(R.layout.layout_custom_text_input_filed_record, null)

        //title
        val t = v.findViewById<TextView>(R.id.titleTv)
        t.text =
            "${transactionField.displayName}${if (transactionField.isRequired == true) "*" else ""}"

        //value
        val e = v.findViewById<EditText>(R.id.valueEt)
        e.tag = transactionField.fieldId

        val i = v.findViewById<TextView>(R.id.errorInfoTv)
        i.tag = transactionField.fieldId + "_i"

        //apply
        transactionField.selected?.value?.let {
            e.setText(it)
        }
        transactionField.selected?.id?.let {
            t?.tag = it
        }

        return Triple(v, t, e)
    }

    fun getSelectedCustomFieldValue(view: View, transactionField: TransactionField): String {
        return when (transactionField.fieldType) {
            COLUMN_TYPE_DROP_DOWN -> (view.findViewWithTag<EditText>(transactionField.fieldId).parent as ConstraintLayout).findViewById<TextView>(
                R.id.titleTv
            ).tag?.toString() ?: ""

            COLUMN_TYPE_CHECK_BOX -> {
                if ((view.findViewWithTag<CheckBox>(transactionField.fieldId)).isChecked) {
                    "true"
                } else {
                    "false"
                }
            }

            else -> view.findViewWithTag<EditText>(transactionField.fieldId).text.toString()
        }
    }

    fun prepareRequest(files: List<TransactionField>, view: View): List<FieldOption>? {
        return files.map {
            FieldOption(
                it.fieldId, getSelectedCustomFieldValue(view, it)
            )
        }
    }

    inner class CustomFieldClickListener(val transactionField: TransactionField) :
        View.OnClickListener {
        override fun onClick(v: View?) {
            OptionsBottomSheetFragment.start(
                supportFragmentManager,
                title = transactionField.displayName ?: "",
                TYPE_CUSTOM_FILED,
                transactionField = transactionField
            )
        }
    }
}