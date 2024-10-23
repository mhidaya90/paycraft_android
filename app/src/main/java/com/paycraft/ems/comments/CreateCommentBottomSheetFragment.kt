package com.paycraft.ems.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.paycraft.R
import com.paycraft.base.BaseBottomSheetDialogFragment
import com.paycraft.base.Validator
import com.paycraft.base.toast
import com.paycraft.databinding.FragmentBottomSheetCreateCommentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCommentBottomSheetFragment : BaseBottomSheetDialogFragment(), View.OnClickListener {
    companion object {
        val TAG = "CreateComment"
        fun start(
            fm: FragmentManager
        ): CreateCommentBottomSheetFragment {
            val fragment = CreateCommentBottomSheetFragment()
            fragment.show(fm, TAG)
            return fragment
        }
    }

    lateinit var mBinding: FragmentBottomSheetCreateCommentBinding
    var mCreateCommentBottomSheetListener: CreateCommentBottomSheetListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_create_comment,
            container,
            false
        )
        mCreateCommentBottomSheetListener = activity as? CreateCommentBottomSheetListener
        mBinding.submitCommentTv.setOnClickListener(this)
        return mBinding.root
    }

    override fun onClick(v: View) {
        val comment = mBinding.commentEt.text.toString().trim()
        if (Validator.isNullOrEmpty(comment)) {
            toast("Please enter comment!")
            return
        }
        mCreateCommentBottomSheetListener?.onSubmitComment(comment)
        dismiss()
    }
}