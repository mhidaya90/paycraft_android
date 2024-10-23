package com.paycraft.card.preferences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.databinding.ActivityPreferencesBinding
import com.paycraft.ems.options_picker.OptionsListener
import com.paycraft.ems.transactions.FieldOption
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreferencesActivity : BaseActivity(), PreferencesFragmentListener, OptionsListener {
    companion object {
        fun start(context: Context) {
            Intent(context, PreferencesActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.run {
                    context.startActivity(this)
                }
        }
    }

    lateinit var binding: ActivityPreferencesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeTitle(getString(R.string.preferences))
    }

    override fun changeTitle(title: String) {
        configToolbar(
            binding.toolbarLayout.toolbar,
            binding.toolbarLayout.toolbarTitleTv, title
        )
    }

    override fun onOptionSelected(type: String, fieldOption: FieldOption) {
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navHostFragment?.childFragmentManager?.fragments?.get(0)?.let {
            (it as AutoTopUpCardFragment).onOptionSelected(
                type,
                fieldOption
            )
        }
    }
}