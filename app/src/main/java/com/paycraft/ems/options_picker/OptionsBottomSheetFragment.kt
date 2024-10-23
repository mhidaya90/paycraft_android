package com.paycraft.ems.options_picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.base.BaseBottomSheetDialogFragment
import com.paycraft.databinding.FragmentBottomSheetOptionsBinding
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.TransactionField

class OptionsBottomSheetFragment
    : BaseBottomSheetDialogFragment(), OptionsListener {
    companion object {
        const val TAG = "OptionsBottomSheetFragment"
        const val E_TITLE = "title";
        const val E_TYPE = "type";
        const val E_IS_LIVE = "isLive";
        const val E_OPTIONS = "options";
        const val E_TRANSACTION_FIELD = "tran_fields";

        const val TYPE_CUSTOM_FILED = "custom_filed";
        const val TYPE_GEN_OPTIONS = "get_options";

        const val TYPE_PURPOSE = "purpose";
        const val TYPE_PURPOSE_OPTION_PERSONAL = "personal";
        const val TYPE_MERCHANTS = "merchants";
        const val TYPE_CATEGORIES = "categories";

        fun start(
            fm: FragmentManager,
            title: String = "",
            type: String = TYPE_GEN_OPTIONS,
            options: ArrayList<FieldOption>? = arrayListOf(),
            transactionField: TransactionField? = null,
            isLive: Boolean = false
        ): OptionsBottomSheetFragment {
            val fragment = OptionsBottomSheetFragment()
            val bundle = Bundle().apply {
                putString(E_TYPE, type)
                putString(E_TITLE, title)
                putBoolean(E_IS_LIVE, isLive)
                transactionField?.let {
                    putParcelable(E_TRANSACTION_FIELD, it)
                }
                options?.let {
                    putParcelableArrayList(E_OPTIONS, it)
                }
            }
            fragment.arguments = bundle
            fragment.show(fm, TAG)
            return fragment
        }
    }

    private var mOptionsAdapter: OptionsAdapter? = null
    private lateinit var mBinding: FragmentBottomSheetOptionsBinding
    private lateinit var viewModel: OptionsBottomSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[OptionsBottomSheetViewModel::class.java]
        viewModel.setView(this)
        viewModel.mType = arguments?.getString(E_TYPE) ?: ""
        viewModel.isLive = arguments?.getBoolean(E_IS_LIVE) ?: false
        viewModel.title = arguments?.getString(E_TITLE) ?: ""

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_options,
            container,
            false
        )
        mBinding.titleTv.text = viewModel.title
        mBinding.titleTv.visibility = if (viewModel.title.isEmpty()) GONE else VISIBLE
        mBinding.searchEt.visibility = if (viewModel.isLive) VISIBLE else GONE
        mBinding.bottomOptionsRv.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mBinding.searchEt.doAfterTextChanged {
            viewModel.getMerchants(it.toString())
        }
        mBinding.addNewTv.setOnClickListener {
            viewModel.createMerchant(mBinding.searchEt.text.toString())
        }

        viewModel.mList.observe(this) { list ->
//            if (null == mOptionsAdapter) {
            mOptionsAdapter = OptionsAdapter(requireContext(), this, list)
            mBinding.bottomOptionsRv.adapter = mOptionsAdapter
//            } else {
//                mOptionsAdapter?.notifyDataSetChanged()
//            }
            mBinding.bottomOptionsRv.visibility = if (list.isEmpty()) GONE else VISIBLE
            mBinding.addNewTv.visibility =
                if (list.isEmpty() && TYPE_MERCHANTS == viewModel.mType) VISIBLE else GONE
        }

        when (viewModel.mType) {
            TYPE_CUSTOM_FILED -> {
                viewModel.mList.postValue(
                    arguments?.getParcelable<TransactionField>(
                        E_TRANSACTION_FIELD
                    )?.options
                )
            }

            TYPE_GEN_OPTIONS -> {
                viewModel.mList.postValue(
                    arguments?.getParcelableArrayList(E_OPTIONS) ?: arrayListOf()
                )
            }

            TYPE_MERCHANTS -> {
                mBinding.optionsRootLl.minimumHeight =
                    requireContext().resources.getDimension(R.dimen.dp500).toInt()
                viewModel.getMerchants(mBinding.searchEt.text.toString())
            }

            TYPE_CATEGORIES -> {
                mBinding.optionsRootLl.minimumHeight =
                    requireContext().resources.getDimension(R.dimen.dp500).toInt()
                viewModel.categories()
            }
        }
        return mBinding.root
    }

    override fun onOptionSelected(type: String, fieldOption: FieldOption) {
        when (viewModel.mType) {
            TYPE_CUSTOM_FILED -> {
                arguments?.getParcelable<TransactionField>(E_TRANSACTION_FIELD)
                    ?.let {
                        (activity as? CustomFieldsOptionsListener)?.onOptionSelected(
                            it,
                            fieldOption
                        )
                    }
            }

            TYPE_CATEGORIES -> {
                (activity as? CategoryListener)?.onOptionSelected(
                    viewModel.categoryObj(fieldOption)
                )
            }

            else -> {
                (activity as? OptionsListener)?.onOptionSelected(
                    viewModel.mType, fieldOption
                )
            }
        }
        dismiss()
    }
}