package com.paycraft.card.cards.balances

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.card.cards.Wallet
import com.paycraft.databinding.RowWalletBinding

class WalletAdapter(var mList: List<Wallet>) :
    RecyclerView.Adapter<WalletAdapter.WalletViewHolder>() {
    fun notifyWithData(list: List<Wallet>) {
        this.mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val binding: RowWalletBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_wallet, parent, false
        )
        return WalletViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int = mList.size

    inner class WalletViewHolder(val mBinding: RowWalletBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(w: Wallet) {
            mBinding.walletNameTv.text = w.walletName ?: "-"
            mBinding.walletBalanceTv.text = w.displayAmount()
        }
    }
}