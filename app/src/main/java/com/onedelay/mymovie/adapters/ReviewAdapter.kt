package com.onedelay.mymovie.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.onedelay.mymovie.R
import com.onedelay.mymovie.data.local.entity.ReviewEntity
import com.onedelay.mymovie.utils.ListDiffCallback
import java.util.*

class ReviewAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<ViewHolder>() {
    private val items = ArrayList<ReviewEntity>()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onRecommendClick(position: Int, value: Int)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // item 을 위한 뷰홀더 객체가 만들어지는 시점에서 자동 호출, 재사용될 수 있는 상태에서는 호출되지 않음.
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.comment_item_view, parent, false)

        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 바인딩 될 시점
        holder.setItem(items[position])
    }

    fun addItem(item: ReviewEntity) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun setItems(items: List<ReviewEntity>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    /**
     * @param newList DB 로부터 새로 불러온 데이터
     */
    fun updateItem(newList: List<ReviewEntity>) {
        val callback = ListDiffCallback(this.items, newList)
        val diffResult = DiffUtil.calculateDiff(callback, false)

        this.items.clear()
        this.items.addAll(newList)

        diffResult.dispatchUpdatesTo(this@ReviewAdapter)
    }

    fun getItem(position: Int): ReviewEntity {
        return items[position]
    }
}
