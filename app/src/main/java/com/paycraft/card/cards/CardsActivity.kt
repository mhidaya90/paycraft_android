package com.paycraft.card.cards

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.ViewPagerAdapter
import com.paycraft.base.toast
import com.paycraft.card.add.kyc.AddCardKycActivity
import com.paycraft.card.add.otp.CARD_ACTIVATION
import com.paycraft.card.add.verify.CardVerificationActivity
import com.paycraft.card.cards.balances.CardBalancesFragment
import com.paycraft.card.cards.balances.TopUpBottomSheetFragmentListener
import com.paycraft.card.cards.balances.TopUpRequest
import com.paycraft.card.cards.settings.CardSettingsFragment
import com.paycraft.card.cart_number.UpdateCardNumberActivity
import com.paycraft.databinding.ActivityCardsBinding
import com.paycraft.ems.TransactionsFragment
import com.paycraft.ems.TransactionsFragmentListener
import com.paycraft.ems.filter.FilterPickerBottomSheetListener
import com.paycraft.ems.options_picker.OptionsListener
import com.paycraft.ems.transactions.FieldOption
import com.paycraft.ems.transactions.Filters
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardsActivity : BaseActivity(), CardsView, View.OnClickListener,
    DiscreteScrollView.OnItemChangedListener<CardsAdapter.CardViewHolder>,
    FilterPickerBottomSheetListener, OptionsListener, TransactionsFragmentListener,
    TopUpBottomSheetFragmentListener {
    companion object {
        val E_CARD = "card"
        val E_INDEX = "card_index"
        fun start(context: Context, cardIndex: Int = 0) {
            Intent(context, CardsActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.putExtra(E_INDEX, cardIndex)
                }.run {
                    context.startActivity(this)
                }
        }
    }

    private lateinit var mCardsViewModel: CardsViewModel
    private lateinit var mCardSettingsFragment: CardSettingsFragment
    private lateinit var mCardBalancesFragment: CardBalancesFragment
    private lateinit var mTransactionsFragment: TransactionsFragment
    lateinit var mBinding: ActivityCardsBinding
    lateinit var mCard: Card
    private var cardsAdapter: CardsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cards)
        mBinding.activateCv.setOnClickListener(this)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, getString(R.string.title_cards)
        )
        mCardsViewModel = ViewModelProvider(this)[CardsViewModel::class.java]
        mCardsViewModel.setView(this)
        mCardsViewModel.mCurrentCard = intent.getIntExtra(E_INDEX, 0)

        mBinding.cardsDsv.adapter = cardsAdapter
        mBinding.cardsDsv.addOnItemChangedListener(this)
        mBinding.cardsDsv.setItemTransformer(
            ScaleTransformer.Builder()
                .build()
        )
        mCardsViewModel.mCardError.observe(this) {
            finish()
        }
        mCardsViewModel.mCards.observe(this) {
            if (it.isEmpty()) {
                toast("No cards Found!")
                finish()
                return@observe
            }
            if (null == cardsAdapter) {
                cardsAdapter = CardsAdapter(it)
                mBinding.cardsDsv.adapter = cardsAdapter
            } else {
                cardsAdapter?.notifyItemChanged(mCardsViewModel.mCurrentCard)
            }
            mBinding.currentAmount.text = it[mCardsViewModel.mCurrentCard].displayBalance()
            it[mCardsViewModel.mCurrentCard].wallets?.let { wallets ->
                if (this::mCardBalancesFragment.isInitialized)
                    mCardBalancesFragment.refresh(
                        wallets
                    )
            }
            mBinding.cardsDsv.scrollToPosition(mCardsViewModel.mCurrentCard)
            if (!mCardsViewModel.isTransactionsOpened)
                buildCardTabs(it[mCardsViewModel.mCurrentCard])
        }
        mCardsViewModel.getCards()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_CANCELED == resultCode) return
        when (requestCode) {
            RC_REFRESH_PREVIOUS_SCREEN -> {
                refreshPreviousScreen =
                    data?.getBooleanExtra(E_REFRESH_PREVIOUS_SCREEN, false) ?: false
                if (!refreshPreviousScreen) return
                mTransactionsFragment.refresh()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reload -> {
                if (mCardsViewModel.isTransactionsOpened) {
                    if (this::mTransactionsFragment.isInitialized)
                        mTransactionsFragment.reload()
                } else {
                    onReload()
                }
                true
            }

            R.id.updateCardDetailsOpn -> {
                mCard.id?.let {
                    UpdateCardNumberActivity.start(this, it)
                }
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activateCv -> {
                mCard.cardReferenceNo?.let {
                    mCardsViewModel.addCardSendOtpAsync(
                        it,
                        CARD_ACTIVATION
                    )
                }
            }
        }
    }

    private fun buildCardTabs(card: Card) {
        mBinding.viewPager.isUserInputEnabled = false
        mBinding.tabs.tabRippleColor = null
        val viewPagerAdapter = ViewPagerAdapter(this)

        if (true == card.wallets?.isNotEmpty()) {
            mCardBalancesFragment = CardBalancesFragment.newInstance(card)
            viewPagerAdapter.addFrag(mCardBalancesFragment, CardBalancesFragment.TAG)
        }

        card.id?.let {
            mTransactionsFragment =
                TransactionsFragment.newInstance(
                    TransactionsFragment.TAG_TRANSACTIONS,
                    cardId = it
                )
            viewPagerAdapter.addFrag(mTransactionsFragment, TransactionsFragment.TAG_TRANSACTIONS)
        }

        if (!card.isHotListed()) {
            mCardSettingsFragment = CardSettingsFragment.newInstance(card)
            viewPagerAdapter.addFrag(mCardSettingsFragment, CardSettingsFragment.TAG)
        }
        mBinding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(
            mBinding.tabs,
            mBinding.viewPager
        ) { tab, position ->
            tab.text = viewPagerAdapter.mFragmentTitleList[position]
        }.attach()

        if (card.isIssuedNotActive()) {
            mBinding.activateCv.visibility = VISIBLE
        } else {
            mBinding.activateCv.visibility = GONE
        }

        mBinding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //mBinding.appbarCardContainer.setExpanded(false)
                mCardsViewModel.isTransactionsOpened =
                    (tab.text.toString() == TransactionsFragment.TAG_TRANSACTIONS)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        if (TransactionsFragment.TAG_TRANSACTIONS == viewPagerAdapter.mFragmentTitleList.firstOrNull())
            mCardsViewModel.isTransactionsOpened = true
    }

    override fun onCurrentItemChanged(
        viewHolder: CardsAdapter.CardViewHolder?,
        adapterPosition: Int
    ) {
        mCardsViewModel.mCards.value?.get(adapterPosition)?.let {
            mCard = it
            buildCardTabs(it)
            mBinding.currentAmount.text = it.displayBalance()
        }
        mCardsViewModel.mCurrentCard = mBinding.cardsDsv.currentItem
    }

    override fun onOtpSent(trxId: String) {
        mCard.cardReferenceNo?.let {
            CardVerificationActivity.start(this, true, id = it, trxId = trxId)
            finish()
        }
    }

    override fun onTopUp(url: String) {
        AddCardKycActivity.start(this, "Top-Up", url, false, false)
    }

    override fun onApplyFilter(list: List<Filters>) {
        mTransactionsFragment.onApplyFilter(list)
    }

    override fun onOptionSelected(type: String, fieldOption: FieldOption) {
        mCardSettingsFragment.onOptionSelected(type, fieldOption)
    }

    override fun onReload() {
        mCardsViewModel.mCurrentCard = mBinding.cardsDsv.currentItem
        mCardsViewModel.getCards()
    }

    override fun onContinue(req: TopUpRequest) {
        req.cardId = mCard.id ?: ""
        mCardsViewModel.cardTopUp(req)
    }
}