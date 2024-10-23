package com.paycraft.card.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.paycraft.R
import com.paycraft.ems.SuccessActivity
import com.paycraft.base.BaseFragment
import com.paycraft.databinding.FragmentAddOnCardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddOnCardFragment : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentAddOnCardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddOnCardBinding.inflate(layoutInflater, container, false)
        binding.cancelTv.setOnClickListener(this)
        binding.saveTv.setOnClickListener(this)
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
                    getString(R.string.request_placed),
                    getString(R.string.request_placed_message)
                )
                requireActivity().finish()
            }
        }
    }
}