package com.paycraft.user.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.dialog
import com.paycraft.databinding.ActivityProfileBinding
import com.paycraft.user.change_passsword.ChangePasswordActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BaseActivity(), ProfileView {
    companion object {
        fun start(context: Context) {
            Intent(context, ProfileActivity::class.java)
                .apply {
                    this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.run {
                    context.startActivity(this)
                }
        }
    }

    private lateinit var mBinding: ActivityProfileBinding
    private lateinit var mViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv, getString(R.string.profile)
        )

        mViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        mViewModel.setView(this)
        mViewModel.mUser.observe(this) {
            mBinding.nameTv.text = it.name ?: "-"
            mBinding.employeeIdTv.text = it.empId ?: "-"
            mBinding.mobileNoTv.text = it.primaryPhone ?: "-"
            mBinding.sMobileNoTv.text = it.secondaryPhone ?: "-"
            mBinding.emailNoTv.text = it.email ?: "-"
            mBinding.rolesTv.text = it.roles?.joinToString { it } ?: "-"
            mBinding.reportingManagerTv.text = it.reportingManager ?: "-"
            mBinding.costCenterTv.text = it.costCenter ?: "-"
            mBinding.projectTv.text = it.project
            mBinding.gradeTv.text = "${it.grade ?: "-"} and ${it.designation ?: "-"}"
            mBinding.nonAgentDataGp.visibility = if (it.isAgent()) GONE else VISIBLE
        }
        mBinding.deleteAccountTv.setOnClickListener {
            dialog(
                "Are you sure you want to delete account?",
                "Yes", "No"
            ) { _, _ ->
                mViewModel.deleteAccount()
            }
        }
        mViewModel.profile()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.changePasswordItem -> {
                ChangePasswordActivity.start(this)
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    override fun onAccountDeleted(message: String) {
        dialog(
            message,
            "Ok", ""
        ) { _, _ ->
            sessionExpired()
        }
    }

    override fun onAccountDeletionFailed(message: String) {
        dialog(
            message,
            "Ok", ""
        ) { _, _ ->
        }
    }
}