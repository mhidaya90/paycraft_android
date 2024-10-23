package com.paycraft.card.cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.card.HomeCardsAdapterListener
import com.paycraft.databinding.RowHomeCardBinding


class HomeCardsAdapter(
    val homeCardsAdapterListener: HomeCardsAdapterListener,
    var list: List<Card>
) : RecyclerView.Adapter<HomeCardsAdapter.CardViewHolder>() {
    fun notifyWithData(cards: List<Card>) {
        list = cards
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding: RowHomeCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_home_card, parent, false
        )
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class CardViewHolder(val mBinding: RowHomeCardBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {
        init {
            mBinding.root.setOnClickListener(this)
            mBinding.activateCardTv.setOnClickListener(this)
            mBinding.addCardCl.setOnClickListener(this)
        }

        fun bind(card: Card) {
            mBinding.homeCard = card
            if (layoutPosition == list.size - 1) {
                mBinding.addCardCl.visibility = View.VISIBLE
            } else {
                mBinding.addCardCl.visibility = View.GONE
            }
            mBinding.executePendingBindings()
        }

        override fun onClick(c: View) {
            when (c.id) {
                R.id.addCardCl -> {
                    homeCardsAdapterListener.addNewCard(list[layoutPosition])
                }
                R.id.activateCardTv -> {
                    homeCardsAdapterListener.onClickActivateCard(list[layoutPosition])
                }
                else -> {
                    homeCardsAdapterListener.onClickHomeCard(list[layoutPosition], layoutPosition)
                }
            }
        }
    }
}