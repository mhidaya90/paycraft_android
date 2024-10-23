package com.paycraft.ems.transactions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import coil.load
import com.paycraft.R
import com.paycraft.base.loadBase64


class AttachmentsAdapter(
    val mContext: Context,
    private var mFiles: List<TransactionFile>,
    val enableDeleteOption: Boolean = false,
    val deleteAttachmentListener: DeleteAttachmentListener? = null,
) : PagerAdapter() {
    var mLayoutInflater: LayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = mFiles.size
    fun notifyWithData(files: List<TransactionFile>) {
        this.mFiles = files
        notifyDataSetChanged()
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View);
    }

    override fun getItemPosition(obj: Any): Int = POSITION_NONE


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View =
            mLayoutInflater.inflate(R.layout.row_transaction_attachment, container, false)

        itemView.findViewById<ImageView>(R.id.deleteAttachmentIv).visibility =
            if (enableDeleteOption) View.VISIBLE else View.GONE
        itemView.findViewById<ImageView>(R.id.deleteAttachmentIv).setOnClickListener {
            if (enableDeleteOption)
                deleteAttachmentListener?.onDeleteFile(mFiles[position])
        }
        itemView.findViewById<ImageView>(R.id.attachmentIv).apply {
            mFiles[position].let {
                when {
                    it.isPdg() -> {
                        load(R.drawable.pdf)
                    }

                    null != it.localFile -> {
                        load(it.localFile)
                    }

                    null != it.base64Data -> {
                        loadBase64(it.base64Data())
                    }

                    else -> {
                        load(R.drawable.ic_profile_place_holder)
                    }
                }
            }
        }
        container.addView(itemView)
        return itemView
    }
}