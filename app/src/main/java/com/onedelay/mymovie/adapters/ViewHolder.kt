package com.onedelay.mymovie.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import com.onedelay.mymovie.data.local.entity.ReviewEntity
import com.onedelay.mymovie.utils.TimeString
import kotlinx.android.synthetic.main.comment_item_view.view.*

class ViewHolder(itemView: View, private val listener: ReviewAdapter.OnItemClickListener) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.review_btn_declare.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }

        /* 리사이클러뷰 아이템의 추천을 눌렀을 경우,
         * 추천수+1 한 값과 현재 아이템의 position 을 콜백메소드에 전달한다. */
        itemView.recommend.setOnClickListener {
            val position = adapterPosition
            val v = Integer.parseInt(itemView.recommend.text.toString().substring(3))

            listener.onRecommendClick(position, v + 1)
        }
    }

    fun setItem(item: ReviewEntity) {
        itemView.apply {
            review_user_id.text = item.getHideWriter()
            review_user_time.text = TimeString.formatTimeString(item.timestamp * 1000)
            review_rating_bar.rating = item.rating
            review_content.text = item.contents
            recommend.text = String.format("추천 %d", item.recommend)
        }
    }
}