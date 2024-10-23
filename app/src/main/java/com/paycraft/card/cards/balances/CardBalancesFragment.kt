package com.paycraft.card.cards.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.base.BaseFragment
import com.paycraft.card.cards.Card
import com.paycraft.card.cards.CardsActivity.Companion.E_CARD
import com.paycraft.card.cards.Wallet
import com.paycraft.databinding.FragmentCardBalanceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardBalancesFragment : BaseFragment(), OnClickListener {
    lateinit var mBinding: FragmentCardBalanceBinding
    var mCard: Card? = null
    var mWalletAdapter: WalletAdapter? = null

    companion object {
        const val TAG = "Balances"
        fun newInstance(card: Card): CardBalancesFragment {
            val args = Bundle()
            val fragment = CardBalancesFragment()
            args.putParcelable(E_CARD, card)
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
            DataBindingUtil.inflate(inflater, R.layout.fragment_card_balance, container, false)
        mCard = arguments?.getParcelable(E_CARD)!!
        mBinding.topUpTv.setOnClickListener(this)
        mCard?.wallets?.let {
            mBinding.walletsRv.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                addItemDecoration(
                    DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
            refresh(it)
        }
        return mBinding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.topUpTv -> {
                TopUpBottomSheetFragment.start(requireActivity().supportFragmentManager)
            }
        }
    }

    fun refresh(list: List<Wallet>) {
        if (mCard?.isActive() == true) {
            val transit = list.find { (it.walletName?.lowercase() ?: "") == "transit" }
            if (null == transit) {
                mWalletAdapter = WalletAdapter(list)
                mBinding.walletsRv.adapter = mWalletAdapter
                mBinding.offlineWalletLl.visibility = GONE
                mBinding.offlineWalletCv.visibility = GONE
            } else {
                mBinding.offlineWalletLl.visibility = VISIBLE
                mBinding.offlineWalletCv.visibility = VISIBLE
                mBinding.walletNameTv.text = transit.walletName
                mBinding.walletBalanceTv.text = transit.balance
                list.filter { (it.walletName?.lowercase() ?: "") != "transit" }.let {
                    if (null == mWalletAdapter) {
                        mWalletAdapter = WalletAdapter(it)
                        mBinding.walletsRv.adapter = mWalletAdapter
                    } else {
                        mWalletAdapter?.notifyWithData(it)
                    }
                }
            }
        } else {
            mWalletAdapter = WalletAdapter(list)
            mBinding.walletsRv.adapter = mWalletAdapter
            mBinding.offlineWalletLl.visibility = GONE
            mBinding.offlineWalletCv.visibility = GONE
        }
    }
}
