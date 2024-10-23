package com.paycraft.card

import com.paycraft.card.cards.Card

interface HomeCardsAdapterListener {
    fun onClickHomeCard(card: Card, cardIndex: Int)
    fun onClickActivateCard(card: Card)
    fun addNewCard(card: Card)
}