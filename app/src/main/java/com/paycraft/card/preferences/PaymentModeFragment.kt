package com.paycraft.card.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.paycraft.R
import com.paycraft.ems.SuccessActivity
import com.paycraft.databinding.FragmentPaymentModeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentModeFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentPaymentModeBinding
    var amount: String? = "0"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentModeBinding.inflate(inflater, container, false)
        binding.cancelTv.setOnClickListener(this)
        binding.saveTv.setOnClickListener(this)
        amount = arguments?.getString("amount")
        binding.amountInfoTv.text = String.format(getString(R.string.amount_message), amount)
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancelTv -> {
                findNavController().popBackStack()
            }
            R.id.saveTv -> {
                SuccessActivity.start(
                    requireContext(),
                    getString(R.string.payment_scheduled),
                    String.format(
                        getString(R.string.amount_message),
                        amount
                    )
                )
            }
        }
    }
}