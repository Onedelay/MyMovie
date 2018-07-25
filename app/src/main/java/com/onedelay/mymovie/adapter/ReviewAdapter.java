package com.onedelay.mymovie.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.onedelay.mymovie.R;
import com.onedelay.mymovie.api.data.ReviewInfo;
import com.onedelay.mymovie.utils.TimeString;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ReviewInfo> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item을 위한 뷰홀더 객체가 만들어지는 시점에서 자동 호출, 재사용될 수 있는 상태에서는 호출되지 않음.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater != null ? inflater.inflate(R.layout.comment_item_view, parent, false) : null;  // parent - item의 최상위 레이아웃

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 바인딩 될 시점
        holder.setItem(items.get(position));
        holder.setOnItemClickListener(listener);
    }

    public void addItem(ReviewInfo item) {
        items.add(item);
    }

    public void addItems(List<ReviewInfo> items) {
        this.items.addAll(items);
    }

    public ReviewInfo getItem(int position) {
        return items.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage;
        private TextView userId;
        private TextView userTime;
        private RatingBar ratingBar;
        private TextView content;
        private TextView recommend;

        OnItemClickListener listener;

        ViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userId = itemView.findViewById(R.id.review_user_id);
            userTime = itemView.findViewById(R.id.review_user_time);
            ratingBar = itemView.findViewById(R.id.review_rating_bar);
            content = itemView.findViewById(R.id.review_content);
            recommend = itemView.findViewById(R.id.recommend);

            itemView.findViewById(R.id.review_btn_declare).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(ReviewInfo item) {
//            if (item.getWriter_image() != null) {
//                Glide.with(context).load(item.getWriter_image()).into(userImage);
//            }
            userId.setText(item.getWriter());
            userTime.setText(TimeString.formatTimeString(item.getTimestamp()));
            ratingBar.setRating(item.getRating());
            content.setText(item.getContents());
            recommend.setText(String.format("추천 %d", item.getRecommend())); // 뷰홀더에서 getString 을 어떻게 호출하나요?
        }

        void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }
}
