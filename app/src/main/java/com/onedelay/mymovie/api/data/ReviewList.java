package com.onedelay.mymovie.api.data;

import java.util.ArrayList;

public class ReviewList {
    private ArrayList<ReviewInfo> result = new ArrayList<>();

    public ArrayList<ReviewInfo> getResult() {
        return result;
    }

    public ReviewInfo get(int index){
        return result.get(index);
    }

    public int size(){
        return result.size();
    }

    public void add(ReviewInfo item){
        result.add(item);
    }
}
