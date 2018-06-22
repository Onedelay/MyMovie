package com.onedelay.mymovie.utils;

import com.onedelay.mymovie.ReviewItem;

import java.util.Comparator;

public class TimeDescending implements Comparator<ReviewItem> {
    @Override
    public int compare(ReviewItem item, ReviewItem t1) {
        return t1.getTime() <= item.getTime() ? -1 : 1;
    }
}
