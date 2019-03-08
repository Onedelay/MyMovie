package com.onedelay.mymovie.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.onedelay.mymovie.R
import java.util.*

class GalleryAdapter(private var listener: OnItemClickListener) : RecyclerView.Adapter<GalleryViewHolder>() {
    private val items = ArrayList<GalleryItem>()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun getItem(position: Int): GalleryItem {
        return items[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.gallery_list_item, parent, false)
        return GalleryViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.setItem(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<GalleryItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: GalleryItem) {
        items.add(item)
    }
}
