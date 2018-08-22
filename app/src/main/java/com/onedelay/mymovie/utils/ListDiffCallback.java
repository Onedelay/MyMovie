package com.onedelay.mymovie.utils;

import android.support.v7.util.DiffUtil;

import com.onedelay.mymovie.database.ReviewEntity;

import java.util.List;

public class ListDiffCallback extends DiffUtil.Callback {
    private final List<ReviewEntity> oldList;
    private final List<ReviewEntity> newList;

    public ListDiffCallback(List<ReviewEntity> oldList, List<ReviewEntity> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getMovieId() == newList.get(newItemPosition).getMovieId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ReviewEntity oldItem = oldList.get(oldItemPosition);
        ReviewEntity newItem = newList.get(newItemPosition);

        return oldItem.equals(newItem);
    }
}
