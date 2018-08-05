package com.onedelay.mymovie.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Reviews")
public class Review {
    @PrimaryKey
    private int reviewId;       // 한줄평 ID
    private String writer;      // 작성자
    private float rating;       // 평점
    private long time;          // 시간
    private int recommend;      // 추천수
}
