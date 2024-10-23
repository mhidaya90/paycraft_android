package com.paycraft.card.cards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowCardBinding


class CardsAdapter(var list: List<Card>) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {
    fun notifyWithData(card: List<Card>) {
        list = card
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding: RowCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_card, parent, false
        )
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class CardViewHolder(val mBinding: RowCardBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(card: Card) {
            mBinding.card = card
            mBinding.textOne.text = card.getFirstFour()
            mBinding.textFour.text = card.getLastFour()
            mBinding.executePendingBindings()
        }
    }
}