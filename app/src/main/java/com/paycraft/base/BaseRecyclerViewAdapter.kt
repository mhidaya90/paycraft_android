package com.paycraft.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T>(var list: List<T>) :
    RecyclerView.Adapter<BaseViewHolder<T>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return getViewHolder(
            LayoutInflater.from(parent.context).inflate(
                getRowView(),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size
    abstract fun getViewHolder(view: View): BaseViewHolder<T>
    abstract fun getRowView(): Int

    fun notifyWithData(list: List<T>) {
        this.list = list;
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(list.get(position))
    }

}

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(t: T)
}