package com.onedelay.mymovie.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.onedelay.mymovie.Constants;
import com.onedelay.mymovie.R;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<GalleryItem> items = new ArrayList<>();

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public GalleryItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater != null ? inflater.inflate(R.layout.gallery_list_item, parent, false) : null;

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItem(items.get(position));
        holder.setOnItemClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<GalleryItem> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void addItem(GalleryItem item) {
        items.add(item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumb;
        private ImageView playIc;


        OnItemClickListener listener;

        public ViewHolder(View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.thumb);
            playIc = itemView.findViewById(R.id.play_ic);

            thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }

        void setItem(GalleryItem item) {
            Glide.with(itemView.getContext()).load(item.getThumbUrl()).into(thumb);
            if (item.getType().equals(Constants.GALLERY_TYPE_MOVIE)) {
                playIc.setVisibility(View.VISIBLE);
            } else {
                playIc.setVisibility(View.INVISIBLE);
            }
        }

        void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }
    }
}
