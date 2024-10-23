package com.paycraft.ems

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.paycraft.R
import com.paycraft.base.BaseActivity
import com.paycraft.base.toast
import com.paycraft.databinding.ActivityAttachmentsViewerBinding
import com.paycraft.ems.transactions.AttachmentsAdapter
import com.paycraft.ems.transactions.DeleteAttachmentListener
import com.paycraft.ems.transactions.TransactionFile
import com.paycraft.file_picker.FilePickerBottomSheetFragment
import com.paycraft.file_picker.FilePickerBottomSheetFragmentListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttachmentsViewerActivity : BaseActivity(), FilePickerBottomSheetFragmentListener,
    DeleteAttachmentListener, View.OnClickListener {
    companion object {
        val E_IMAGEAS = "images"
        val E_DELETED_IMAGEAS = "deleted_images"
        val RC = 211
        fun start(activity: Activity, images: ArrayList<TransactionFile>) {
            Intent(activity, AttachmentsViewerActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                this.putParcelableArrayListExtra(E_IMAGEAS, images)
            }.run {
                activity.startActivityForResult(this, RC)
            }
        }
    }

    private lateinit var mBinding: ActivityAttachmentsViewerBinding
    private lateinit var mFiles: ArrayList<TransactionFile>
    private lateinit var mDeletedFiles: ArrayList<TransactionFile>
    private lateinit var mTransactionAttachmentsPagerAdapter: AttachmentsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_attachments_viewer)
        configToolbar(
            mBinding.toolbarLayout.toolbar,
            mBinding.toolbarLayout.toolbarTitleTv,
            getString(R.string.receipts)
        )
        mBinding.addImageIv.setOnClickListener(this)
        mBinding.retakeIv.setOnClickListener(this)
        mBinding.adjustIv.setOnClickListener(this)
        mBinding.doneTv.setOnClickListener(this)

        mFiles =
            intent.getParcelableArrayListExtra<TransactionFile>(E_IMAGEAS) as ArrayList<TransactionFile>
        mDeletedFiles = arrayListOf()

        mTransactionAttachmentsPagerAdapter = AttachmentsAdapter(
            this,
            intent.getParcelableArrayListExtra(E_IMAGEAS) ?: arrayListOf(),
            false,
            this
        )
        mBinding.imagesVp.adapter = mTransactionAttachmentsPagerAdapter
        mBinding.indiacatorTabLayout.setupWithViewPager(mBinding.imagesVp, true)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.doneTv -> {
                Intent().apply {
                    this.putExtra(E_IMAGEAS, mFiles)
                    this.putExtra(E_DELETED_IMAGEAS, mDeletedFiles)
                }.run {
                    setResult(RESULT_OK, this)
                }
                finish()
            }

            R.id.addImageIv -> {
                if (mFiles.size == 5) {
                    toast(getString(R.string.maximum_of_attachments))
                    return
                }
                FilePickerBottomSheetFragment.start(supportFragmentManager)
            }

            R.id.retakeIv -> {
                FilePickerBottomSheetFragment.start(supportFragmentManager)
            }

            R.id.adjustIv -> {
                FilePickerBottomSheetFragment.start(supportFragmentManager)
            }
        }
    }

    override fun onFileSelected(file: TransactionFile) {
        mFiles.add(file)
        mTransactionAttachmentsPagerAdapter.notifyWithData(mFiles)
        mBinding.imagesVp.currentItem = mFiles.lastIndex
    }

    override fun onDeleteFile(file: TransactionFile) {
        mDeletedFiles.add(file)
        mFiles.remove(file)
        mTransactionAttachmentsPagerAdapter.notifyWithData(mFiles)
    }
}