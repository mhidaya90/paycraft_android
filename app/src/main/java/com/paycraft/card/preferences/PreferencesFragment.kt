package com.paycraft.card.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.paycraft.R
import com.paycraft.base.BaseFragment
import com.paycraft.databinding.FragmentPreferencesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreferencesFragment : BaseFragment(), View.OnClickListener {
    lateinit var binding: FragmentPreferencesBinding
    lateinit var mListener: PreferencesFragmentListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mListener = requireActivity() as PreferencesFragmentListener
        binding = FragmentPreferencesBinding.inflate(layoutInflater, container, false)
        binding.autoTopUpCardTv.setOnClickListener(this)
        binding.addOnCardTv.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.autoTopUpCardTv -> {
                mListener.changeTitle("Choose payment mode")
                findNavController().navigate(R.id.autoTopUpCardFragment)
            }
            R.id.addOnCardTv -> {
                mListener.changeTitle("Add on Card")
                findNavController().navigate(R.id.addOnCard)
            }
        }
    }
}