package com.onedelay.mymovie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    Context context;
    ArrayList<ReviewItem> items = new ArrayList<>();
    OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(RecyclerView.ViewHolder holder, View view, int position);
    }

    public ReviewAdapter(Context context){
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
        View itemView = inflater.inflate(R.layout.comment_item_view, parent, false);  // parent - item의 최상위 레이아웃

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 바인딩 될 시점
        holder.setItem(items.get(position));

        holder.setOnItemClickListener(listener);
    }

    public void addItem(ReviewItem item){
        items.add(item);
    }

    public void addItems(ArrayList<ReviewItem> items){
        this.items = items;
    }

    public ReviewItem getItem(int position){
        return items.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage;
        private TextView userId;
        private TextView userTime;
        private RatingBar ratingBar;
        private TextView content;
        private TextView recommend;
        private View line;
        private TextView declareBtn;

        OnItemClickListener listener;

        public ViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userId = itemView.findViewById(R.id.review_user_id);
            userTime = itemView.findViewById(R.id.review_user_time);
            ratingBar = itemView.findViewById(R.id.review_rating_bar);
            content = itemView.findViewById(R.id.review_content);
            recommend = userTime = itemView.findViewById(R.id.recommend);
            line = itemView.findViewById(R.id.view);
            declareBtn = itemView.findViewById(R.id.review_btn_declare);

            itemView.findViewById(R.id.review_btn_declare).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(ReviewItem item){
            userImage.setImageResource(item.getImage());
            userId.setText(item.getId());
            userTime.setText(item.getTime());
            ratingBar.setRating(item.getRating());
            content.setText(item.getContent());
            recommend.setText(item.getRecommend());
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
    }
}
