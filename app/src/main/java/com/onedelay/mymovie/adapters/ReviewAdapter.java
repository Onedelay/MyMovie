package com.onedelay.mymovie.adapters;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.onedelay.mymovie.R;
import com.onedelay.mymovie.database.ReviewEntity;
import com.onedelay.mymovie.utils.ListDiffCallback;
import com.onedelay.mymovie.utils.TimeString;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ReviewEntity> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onRecommendClick(int position, int value);
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

    public void addItem(ReviewEntity item) {
        items.add(item);
    }

    public void setItems(List<ReviewEntity> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    /**
     * @param newList DB 로부터 새로 불러온 데이터
     */
    public void updateItem(List<ReviewEntity> newList){
        ListDiffCallback callback = new ListDiffCallback(this.items, newList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback, false);

        this.items.clear();
        this.items.addAll(newList);

        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                diffResult.dispatchUpdatesTo(ReviewAdapter.this);
            }
        });
    }

    public ReviewEntity getItem(int position) {
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

        ViewHolder(final View itemView) {
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
                        listener.onItemClick(position);
                    }
                }
            });

            /* 리사이클러뷰 아이템의 추천을 눌렀을 경우,
             * 추천수+1 한 값과 현재 아이템의 position 을 콜백메소드에 전달한다. */
            recommend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    int v = Integer.parseInt(recommend.getText().toString().substring(3));

                    if (listener != null) {
                        listener.onRecommendClick(position, v+1);
                    }
                }
            });
        }

        void setItem(ReviewEntity item) {
            userId.setText(item.getWriter());
            userTime.setText(TimeString.formatTimeString(item.getTimestamp()*1000));
            ratingBar.setRating(item.getRating());
            content.setText(item.getContents());
            recommend.setText(String.format("추천 %d", item.getRecommend())); // 뷰홀더에서 getString 을 어떻게 호출하나요?
        }

        void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }
}
