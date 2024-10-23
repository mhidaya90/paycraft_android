package com.paycraft.card.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.paycraft.R
import com.paycraft.base.BaseFragment
import com.paycraft.base.toast
import com.paycraft.databinding.FragmentAutoTopUpCardBinding
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.transactions.FieldOption
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutoTopUpCardFragment : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentAutoTopUpCardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAutoTopUpCardBinding.inflate(layoutInflater, container, false)
        binding.startDateEt.setOnClickListener(this)
        binding.endNameEt.setOnClickListener(this)
        binding.firstPaymentToArriveOnEt.setOnClickListener(this)
        binding.frequencyOfTopUpEt.setOnClickListener(this)
        binding.cancelTv.setOnClickListener(this)
        binding.saveTv.setOnClickListener(this)
        binding.frequencyOfTopUpEt.setOnClickListener(this)
        binding.topUpOptionsRg.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.minimumCardBalanceRb -> {
                    binding.minCardBalGp.visibility = View.VISIBLE
                    binding.recurringPaymentGp.visibility = View.GONE
                }
                R.id.recurringPaymentRb -> {
                    binding.minCardBalGp.visibility = View.GONE
                    binding.recurringPaymentGp.visibility = View.VISIBLE
                }
            }
        }
        binding.topUpOptionsRg.check(R.id.minimumCardBalanceRb)
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancelTv -> {
                findNavController().popBackStack()
            }
            R.id.saveTv -> {
                val amount = binding.topUpAmountEt.text.toString()
                if (amount.isEmpty()) {
                    toast("Amount is empty!")
                    return
                }
                val bundle = Bundle()
                bundle.putString("amount", amount)
                findNavController().navigate(R.id.paymentModeFragment, bundle)
            }
            R.id.frequencyOfTopUpEt -> {
                val options = arrayListOf(
                    FieldOption("monthly", "Monthly"),
                    FieldOption("weekly", "Weekly")
                )
                OptionsBottomSheetFragment.start(
                    childFragmentManager,
                    options = options
                )
            }
            R.id.firstPaymentToArriveOnEt -> {
                dateTimeDialog(
                    binding.firstPaymentToArriveOnEt,
                    isDateEnabled = true,
                    isTimeEnabled = false
                )
            }
            R.id.startDateEt -> {
                dateTimeDialog(binding.startDateEt, isDateEnabled = true, isTimeEnabled = false)
            }
            R.id.endNameEt -> {
                dateTimeDialog(binding.endNameEt, isDateEnabled = true, isTimeEnabled = false)
            }
        }
    }

    fun onOptionSelected(type: String, fieldOption: FieldOption) {
        fieldOption.let {
            binding.frequencyOfTopUpEt.setText(it.value ?: "")
            binding.frequencyOfTopUpEt.tag = it.id ?: ""
        }
    }
}