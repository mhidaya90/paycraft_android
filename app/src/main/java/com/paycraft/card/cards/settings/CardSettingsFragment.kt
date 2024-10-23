package com.paycraft.card.cards.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.paycraft.R
import com.paycraft.base.BaseFragment
import com.paycraft.base.dialog
import com.paycraft.base.toast
import com.paycraft.card.add.kyc.AddCardKycActivity
import com.paycraft.card.cards.Card
import com.paycraft.card.cards.CardsActivity.Companion.E_CARD
import com.paycraft.databinding.FragmentCardSettingsBinding
import com.paycraft.ems.TransactionsFragmentListener
import com.paycraft.ems.options_picker.OptionsBottomSheetFragment
import com.paycraft.ems.options_picker.OptionsListener
import com.paycraft.ems.transactions.FieldOption
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardSettingsFragment : BaseFragment(), CardSettingsView, View.OnClickListener,
    OptionsListener {
    lateinit var mBinding: FragmentCardSettingsBinding
    lateinit var mViewModel: CardSettingsViewModel
    lateinit var mCard: Card

    companion object {
        const val TAG = "Settings"
        fun newInstance(card: Card): CardSettingsFragment {
            val args = Bundle()
            args.putParcelable(E_CARD, card)
            val fragment = CardSettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_card_settings, container, false)
        mBinding.blockCardBtn.setOnClickListener(this)
        mBinding.changePosPinActionTv.setOnClickListener(this)
        mViewModel = ViewModelProvider(this)[CardSettingsViewModel::class.java]
        mViewModel.setView(this)
        mCard = arguments?.getParcelable(E_CARD)!!
        mBinding.blockCardCl.visibility =
            if (mCard.isActive() || mCard.isThl() || mCard.isInActive()) VISIBLE
            else GONE
        mViewModel.mReasons.observe(viewLifecycleOwner) {
            OptionsBottomSheetFragment.start(
                childFragmentManager,
                title = "Reasons",
                OptionsBottomSheetFragment.TYPE_GEN_OPTIONS,
                options = it as ArrayList<FieldOption>?
            )
        }
        return mBinding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.changePosPinActionTv -> {
                mCard.id?.let { mViewModel.changePosPin(it) }
            }

            R.id.blockCardBtn -> {
                mCard.id?.let {
                    mViewModel.blockCardReasons(it)
                }
            }
        }
    }

    override fun onBlockCard() {
        toast("Blocked Successfully!")
        (activity as TransactionsFragmentListener).onReload()
    }

    override fun onSetPinSuccess(url: String) {
        AddCardKycActivity.start(requireContext(), getString(R.string.title_pos), url)
    }

    override fun onOptionSelected(type: String, fieldOption: FieldOption) {
        dialog(
            getString(R.string.delete_block_card),
            getString(R.string.dialog_yes),
            getString(R.string.dialog_no)
        ) { _, _ ->
            fieldOption.value?.let { r ->
                mCard.id?.let { mViewModel.blockCard(it, r) };
            }
        }
    }
}