package com.paycraft.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.paycraft.R
import com.paycraft.databinding.RowNotificationBinding

class NotificationAdapter(
    var mNotifications: List<PaycraftNotification>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun notifyWithData(notifications: List<PaycraftNotification>) {
        mNotifications = notifications
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: RowNotificationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_notification,
            parent,
            false
        )
        return NotificationViewMode(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NotificationViewMode).bind(mNotifications[position])
    }

    override fun getItemCount(): Int = mNotifications.size

    inner class NotificationViewMode(val mBinding: RowNotificationBinding) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {
        fun bind(notification: PaycraftNotification) {
            mBinding.notification = notification
            mBinding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mNotifications[layoutPosition].markAsSeen()
            notifyItemChanged(layoutPosition)
            (v.context as? NotificationAdapterListener)?.onClickNotification(mNotifications[layoutPosition])
        }
    }
}

@BindingAdapter("android:setViewBackgroundColor")
fun setViewBackgroundColor(view: View, isSelected: Boolean) {
    view.setBackgroundColor(
        if (isSelected)
            ContextCompat.getColor(
                view.context,
                R.color.white
            )
        else
            ContextCompat.getColor(
                view.context,
                R.color.colorFloralWhite
            )
    )
}
