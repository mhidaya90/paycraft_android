package com.paycraft.file_picker

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.paycraft.R
import com.paycraft.base.BaseBottomSheetDialogFragment
import com.paycraft.base.toast
import com.paycraft.databinding.FragmentBottomSheetFilePickerBinding
import com.paycraft.ems.transactions.TransactionFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class FilePickerBottomSheetFragment : BaseBottomSheetDialogFragment(), View.OnClickListener {
    val FILE_SIZE = 500L

    companion object {
        val TAG = "FilePickerBottomSheetFragment"
        fun start(fm: FragmentManager): FilePickerBottomSheetFragment {
            val fragment = FilePickerBottomSheetFragment()
            fragment.show(fm, TAG)
            return fragment
        }
    }

    private lateinit var mBinding: FragmentBottomSheetFilePickerBinding
    private var mFilePickerListener: FilePickerBottomSheetFragmentListener? = null
    private lateinit var mUri: Uri
    private lateinit var mFile: File
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                mBinding.cameraTv.performClick()
            } else {
                toast("Camera permission required to capture!")
            }
        }
    private val mCapturePictureResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
            if (captured) {
                GlobalScope.launch {
                    compressImageTo(requireContext().applicationContext, mFile, FILE_SIZE)?.let {
                        mFile = it
                    }
                    withContext(Dispatchers.Main) {
                        val file = TransactionFile(
                            mFile,
                            mFile.mimeType(requireContext().applicationContext),
                            mFile.extension
                        )
                        mFilePickerListener?.onFileSelected(file)
                        dismiss()
                    }
                }
            }
        }
    private val mFilePickerResult =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            it?.let {
                mFile = newFile(requireContext(), ".jpg")
                it.fileFromUri(mFile, requireContext().applicationContext)
                lifecycleScope.launch {
                    compressImageTo(requireContext().applicationContext, mFile, FILE_SIZE)?.let {
                        mFile = it
                    }
                    withContext(Dispatchers.Main) {
                        val file =
                            TransactionFile(
                                mFile,
                                mFile.mimeType(requireContext().applicationContext),
                                mFile.extension
                            )
                        mFilePickerListener?.onFileSelected(file)
                        dismiss()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFilePickerListener = activity as? FilePickerBottomSheetFragmentListener
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_file_picker,
            container,
            false
        )
        mBinding.cameraTv.setOnClickListener(this)
        mBinding.galleryTv.setOnClickListener(this)
        return mBinding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cameraTv -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    activityResultLauncher.launch(Manifest.permission.CAMERA)
                    return
                }
                mFile = newFile(requireContext(), ".jpg")
                mUri = mFile.uriFromFile(requireContext())
                mCapturePictureResult.launch(mUri)
            }

            R.id.galleryTv -> {
                mFilePickerResult.launch(arrayOf(/*"application/pdf",*/ "image/*"))
            }
        }
    }
}