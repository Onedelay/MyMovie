package com.onedelay.mymovie.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.onedelay.mymovie.Constants
import kotlinx.android.synthetic.main.gallery_list_item.view.*

class GalleryViewHolder(itemView: View, private val listener: GalleryAdapter.OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
    init {
        itemView.thumb.setOnClickListener { listener.onItemClick(adapterPosition) }
    }

    fun setItem(item: GalleryItem) {
        Glide.with(itemView.context).load(item.thumbUrl).into(itemView.thumb)
        itemView.play_ic.visibility = if (item.type == Constants.GALLERY_TYPE_MOVIE) View.VISIBLE else View.INVISIBLE
    }
}
