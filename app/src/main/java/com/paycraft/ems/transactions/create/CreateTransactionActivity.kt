package com.paycraft.ems.transactions.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Base64OutputStream
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.paycraft.R
import com.paycraft.base.DecimalDigitsInputFilter
import com.paycraft.base.loadBase64
import com.paycraft.databinding.ActivityCreateTransactionBinding
import com.paycraft.ems.AttachmentsViewerActivity
import com.paycraft.ems.AttachmentsViewerActivity.Companion.E_DELETED_IMAGEAS
import com.paycraft.ems.AttachmentsViewerActivity.Companion.E_IMAGEAS
import com.paycraft.ems.options_picker.CategoryListener
import com.paycraft.ems.options_picker.CustomFieldsOptionsListener
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment.Companion.TYPE_CATEGORIES
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment.Companion.TYPE_MERCHANTS
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment.Companion.TYPE_PURPOSE
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment.Companion.TYPE_PURPOSE_OPTION_PERSONAL
import com.paycraft.ems.options_picker.OptionsListener
import com.paycraft.ems.transactions.Category
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.Transaction
import com.paycraft.ems.transactions.TransactionField
import com.paycraft.ems.transactions.TransactionFields.Companion.TRANSACTION_FILED_AMOUNT
import com.paycraft.ems.transactions.TransactionFields.Companion.TRANSACTION_FILED_CATEGORY_ID
import com.paycraft.ems.transactions.TransactionFields.Companion.TRANSACTION_FILED_CLAIM_REIMBURSABLE_ID
import com.paycraft.ems.transactions.TransactionFields.Companion.TRANSACTION_FILED_DATE
import com.paycraft.ems.transactions.TransactionFields.Companion.TRANSACTION_FILED_MERCHANT
import com.paycraft.ems.transactions.TransactionFile
import com.paycraft.ems.transactions.detail.TransactionActivity.Companion.E_TRANSACTION_ID
import com.paycraft.file_picker.FilePickerBottomSheetFragmentListener
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale

@AndroidEntryPoint
class CreateTransactionActivity : CreateRecordBaseActivity(), View.OnClickListener,
    CreateTransactionView, CustomFieldsOptionsListener, FilePickerBottomSheetFragmentListener,
    OptionsListener, CategoryListener {
    companion object {
        const val E_TRANSACTION = "transaction"
        const val E_CARD_TRANSACTION = "card_transaction"
        const val RC = 101
        fun start(activity: Activity, id: String = "", isCardTransaction: Boolean = false) {
            Intent(activity, CreateTransactionActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                this.putExtra(E_TRANSACTION_ID, id)
                this.putExtra(E_CARD_TRANSACTION, isCardTransaction)
            }.run {
                activity.startActivityForResult(this, RC)
            }
        }
    }

    lateinit var viewModel: CreateTransactionViewModel
    lateinit var binding: ActivityCreateTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_transaction)
        configToolbar(
            binding.toolbarLayout.toolbar,
            binding.toolbarLayout.toolbarTitleTv,
            getString(R.string.add_expense)
        )
        binding.cancelTv.setOnClickListener(this)
        binding.saveTv.setOnClickListener(this)
        binding.pickImageIv.setOnClickListener(this)
        binding.imageCountTv.setOnClickListener(this)
        binding.dateAndTimeEt.setOnClickListener(this)
        binding.merchantEt.setOnClickListener(this)
        binding.categoryEt.setOnClickListener(this)
        binding.amountEt.filters = arrayOf(DecimalDigitsInputFilter(7, 2))
        viewModel = ViewModelProvider(this)[CreateTransactionViewModel::class.java]
        viewModel.setView(this)
        viewModel.id = intent.getStringExtra(E_TRANSACTION_ID) ?: ""
        viewModel.isCardTransaction = intent.getBooleanExtra(E_CARD_TRANSACTION, false)
        if (viewModel.isCardTransaction == true) {
            binding.dateAndTimeEt.isEnabled = false
            binding.dateAndTimeEt.alpha = 0.5f
            binding.merchantEt.isEnabled = false
            binding.merchantEt.alpha = 0.5f
            binding.amountEt.isEnabled = false
            binding.amountEt.alpha = 0.85f
        }

        binding.dateAndTimeEt.addTextChangedListener {
            binding.dateAndTimeErrorInfoTv.visibility = GONE
        }
        binding.merchantEt.addTextChangedListener {
            binding.merchantErrorInfoTv.visibility = GONE
        }
        binding.categoryEt.addTextChangedListener {
            binding.categoryErrorInfoTv.visibility = GONE
        }
        binding.amountEt.addTextChangedListener {
            binding.amountErrorInfoTv.visibility = GONE
        }

        viewModel.staticFields.observe(this) { f ->
            //static
            binding.amountEt.setText(
                f.find { TRANSACTION_FILED_AMOUNT == it.fieldId }?.selected?.value ?: ""
            )
            binding.dateAndTimeEt.setText(
                f.find { TRANSACTION_FILED_DATE == it.fieldId }?.selected?.value ?: ""
            )
            f.find { TRANSACTION_FILED_MERCHANT == it.fieldId }?.selected?.let { m ->
                binding.merchantEt.setText(m.value)
                binding.merchantEt.tag = m.id
            }
            f.find { TRANSACTION_FILED_CATEGORY_ID == it.fieldId }?.selected?.let {
                binding.categoryEt.setText(it.value)
                binding.categoryEt.tag = it.id
            }

            viewModel.isCategoryChangedByUser = false
        }
        viewModel.fields.observe(this) { f ->
            //dynamic
            f.forEach { f ->
                createField(f)?.also {
                    binding.transactionFieldsLl.addView(it)
                }
            }
            if (TYPE_PURPOSE_OPTION_PERSONAL == (f.find { it.fieldName == TYPE_PURPOSE }?.selected?.value?.lowercase()
                    ?: "")
            ) {
                binding.claimCb.isChecked = false
                binding.claimCb.isEnabled = false
            } else {
                binding.claimCb.isChecked = "true" == (viewModel.staticFields.value?.find {
                    TRANSACTION_FILED_CLAIM_REIMBURSABLE_ID == it.fieldId
                }?.selected?.value ?: "")
            }
        }

        viewModel.files.observe(this) {
            updateFileIndicator()
        }
        viewModel.getTransactionFields()
        if (viewModel.id.isNullOrEmpty()) {
            binding.loadingSkv.visibility = GONE
            binding.pickImageIv.visibility = VISIBLE
            binding.selectedGroup.visibility = GONE
        } else {
            viewModel.getTransactionAttachments()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK != resultCode) return
        when (requestCode) {
            AttachmentsViewerActivity.RC -> {
                viewModel.files.postValue(
                    data?.getParcelableArrayListExtra(
                        E_IMAGEAS
                    ) ?: mutableListOf()
                )
                viewModel.deletedFiles.addAll(
                    data?.getParcelableArrayListExtra(
                        E_DELETED_IMAGEAS
                    ) ?: mutableListOf()
                )
                updateFileIndicator()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.merchantEt -> {
                OptionsBottomSheetFragment.start(
                    supportFragmentManager,
                    title = "Merchant",
                    TYPE_MERCHANTS,
                    isLive = true
                )
            }

            R.id.categoryEt -> {
                OptionsBottomSheetFragment.start(
                    supportFragmentManager, title = "Category", TYPE_CATEGORIES
                )
            }

            R.id.dateAndTimeEt -> {
                dateTimeDialog(
                    binding.dateAndTimeEt, true, true
                )
            }

            R.id.imageCountTv, R.id.pickImageIv -> {
                AttachmentsViewerActivity.start(
                    this,
                    viewModel.files.value as? ArrayList<TransactionFile> ?: arrayListOf()
                )
            }

            R.id.cancelTv -> {
                onBackPressed()
            }

            R.id.saveTv -> {
                val amount = binding.amountEt.text.toString()
                val dateAndTime = binding.dateAndTimeEt.text.toString()
                val merchant = binding.merchantEt.text.toString()
                val category = binding.categoryEt.text.toString()

                if (amount.isEmpty()) {
                    binding.amountErrorInfoTv.apply {
                        visibility = VISIBLE
                        text = "Please enter amount!"
                    }
                    return
                }
                val amt = amount.toDouble()
                if (amt == 0.0) {
                    binding.amountErrorInfoTv.apply {
                        visibility = VISIBLE
                        text = "Amount can not be zero!"
                    }
                    return
                }
                if (dateAndTime.isEmpty()) {
                    binding.dateAndTimeErrorInfoTv.apply {
                        visibility = VISIBLE
                        text = "Please enter Date and Time!"
                    }
                    return
                }
                if (merchant.isEmpty()) {
                    binding.merchantErrorInfoTv.apply {
                        visibility = VISIBLE
                        text = "Please select Merchant!"
                    }
                    return
                }
                if (category.isEmpty()) {
                    binding.categoryErrorInfoTv.apply {
                        visibility = VISIBLE
                        text = "Please select Category!"
                    }
                    return
                }

                if (!isCustomFieldsOk(
                        viewModel.fields.value,
                        binding.transactionFieldsLl
                    )
                ) {
                    return
                }
                viewModel.createTransaction(
                    prepareRequest(),
                    prepareFiles(),
                    prepareDeletedFiles(),
                    intent.getStringExtra(E_TRANSACTION_ID) ?: ""
                )
            }
        }
    }

    override fun onCreateTransactionSuccess(transaction: Transaction) {
        intent.apply {
            refreshPreviousScreen()
            putExtra(E_REFRESH_PREVIOUS_SCREEN, refreshPreviousScreen)
            putExtra(E_TRANSACTION, transaction)
            putExtra(E_TRANSACTION_ID, transaction.id)
        }.run {
            setResult(RESULT_OK, this)
        }
        finish()
    }


    override fun onFileSelected(file: TransactionFile) {
        viewModel.files.value?.add(file)
        updateFileIndicator()
    }

    override fun onOptionSelected(
        transactionField: TransactionField, fieldOption: FieldOption
    ) {
        binding.transactionFieldsLl.findViewWithTag<EditText>(transactionField.fieldId)
            ?.setText(fieldOption.value ?: "")
        (binding.transactionFieldsLl.findViewWithTag<EditText>(transactionField.fieldId)?.parent as ConstraintLayout?)?.findViewById<TextView>(
            R.id.titleTv
        )?.tag = fieldOption.id ?: ""

        if (transactionField.fieldName == TYPE_PURPOSE) {
            if (fieldOption.value?.lowercase() == TYPE_PURPOSE_OPTION_PERSONAL) {
                binding.claimCb.isChecked = false
                binding.claimCb.isEnabled = false
            } else {
                binding.claimCb.isEnabled = true
                binding.claimCb.isChecked = true
            }
        }
    }

    override fun onOptionSelected(type: String, fieldOption: FieldOption) {
        when (type) {
            TYPE_MERCHANTS -> {
                binding.merchantEt.setText(fieldOption.value)
                binding.merchantEt.tag = fieldOption.id
            }
        }
    }

    override fun onOptionSelected(category: Category?) {
        category?.let {
            viewModel.category = it
            binding.categoryEt.setText(it.name)
            binding.categoryEt.tag = it.id
            binding.transactionFieldsLl.removeAllViews()
            viewModel.isCategoryChangedByUser = true
            viewModel.getTransactionFields()
        }
    }

    private fun prepareFiles(): List<MultipartBody.Part> {
        val mRequest = arrayListOf<MultipartBody.Part>()
        viewModel.files.value?.forEachIndexed { i, item ->
            item.localFile?.let { lf ->
                mRequest.add(
                    MultipartBody.Part.createFormData(
                        String.format(Locale.US, "files[%d]", i),
                        convertImageFileToBase64(lf)
                    )
                )
            }
        }
        return mRequest
    }

    private fun convertImageFileToBase64(imageFile: File): String {
        return ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                imageFile.inputStream().use { inputStream ->
                    inputStream.copyTo(base64FilterStream)
                }
            }
            return@use outputStream.toString()
        }
    }

    private fun prepareDeletedFiles(): List<MultipartBody.Part> {
        val mRequest = arrayListOf<MultipartBody.Part>()
        viewModel.deletedFiles.forEachIndexed { index, transactionField ->
            if (null != transactionField.id) {
                mRequest.add(
                    MultipartBody.Part.createFormData(
                        String.format(Locale.US, "delete_file_ids[%d]", index),
                        transactionField.id
                    )
                )
            }
        }
        return mRequest
    }

    private fun prepareRequest(): List<MultipartBody.Part> {
        val mRequest = arrayListOf<MultipartBody.Part>()
        viewModel.fields.value?.forEachIndexed { index, transactionField ->
            transactionField.fieldId?.let {
                mRequest.add(
                    MultipartBody.Part.createFormData(
                        "field_values[${index}][id]",
                        it
                    )
                )
                mRequest.add(
                    MultipartBody.Part.createFormData(
                        "field_values[${index}][value]",
                        getSelectedCustomFieldValue(
                            binding.transactionFieldsLl,
                            transactionField
                        )
                    )
                )
            }
        }

        var nextIndex = mRequest.size - 1
        //amount
        mRequest.add(
            MultipartBody.Part.createFormData(
                "field_values[${nextIndex}][id]", TRANSACTION_FILED_AMOUNT
            )
        )
        mRequest.add(
            MultipartBody.Part.createFormData(
                "field_values[${nextIndex}][value]", binding.amountEt.text.toString()
            )
        )

        nextIndex += 1
        //date
        mRequest.add(
            MultipartBody.Part.createFormData(
                "field_values[${nextIndex}][id]", TRANSACTION_FILED_DATE
            )
        )
        mRequest.add(
            MultipartBody.Part.createFormData(
                "field_values[${nextIndex}][value]", binding.dateAndTimeEt.text.toString()
            )
        )

        nextIndex += 1
        //merchant
        mRequest.add(
            MultipartBody.Part.createFormData(
                "field_values[${nextIndex}][id]", TRANSACTION_FILED_MERCHANT
            )
        )
        binding.merchantEt.tag?.toString()?.let {
            mRequest.add(
                MultipartBody.Part.createFormData(
                    "field_values[${nextIndex}][value]", it
                )
            )
        }

        nextIndex += 1
        //category
        mRequest.add(
            MultipartBody.Part.createFormData(
                "field_values[${nextIndex}][id]", TRANSACTION_FILED_CATEGORY_ID
            )
        )
        mRequest.add(
            MultipartBody.Part.createFormData(
                "field_values[${nextIndex}][value]", binding.categoryEt.tag.toString()
            )
        )

        nextIndex += 1
        //category
        mRequest.add(
            MultipartBody.Part.createFormData(
                "field_values[${nextIndex}][id]", TRANSACTION_FILED_CLAIM_REIMBURSABLE_ID
            )
        )
        mRequest.add(
            MultipartBody.Part.createFormData(
                "field_values[${nextIndex}][value]", binding.claimCb.isChecked.toString()
            )
        )
        return mRequest
    }

    private fun updateFileIndicator() {
        viewModel.files.value?.lastOrNull()?.let {
            binding.loadingSkv.visibility = GONE

            val size = viewModel.files.value?.size ?: 0
            binding.imageCountTv.text = "+${size}"

            if (size == 0) {
                binding.pickImageIv.visibility = VISIBLE
                binding.selectedGroup.visibility = GONE
            } else {
                binding.selectedGroup.visibility = VISIBLE
                binding.pickImageIv.visibility = GONE
            }
            if (null == it.localFile) {
                binding.selectedImageIv.loadBase64(it.base64Data())
            } else {
                binding.selectedImageIv.load(it.localFile)
            }
        } ?: run {
            binding.loadingSkv.visibility = GONE
            binding.pickImageIv.visibility = VISIBLE
            binding.selectedGroup.visibility = GONE
        }
    }

}