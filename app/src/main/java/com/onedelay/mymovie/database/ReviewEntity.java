package com.onedelay.mymovie.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;

@Entity(tableName = "Reviews")
public class ReviewEntity {
    @PrimaryKey
    private int id;                 // 한줄평 ID
    private int movieId;            // 영화 ID
    private String writer_image;    // 작성자 프로필 이미지
    private String writer;          // 작성자
    private float rating;           // 평점
    private String time;            // 시간
    private long timestamp;         // 시간(unix)
    private int recommend;          // 추천수
    private String contents;        // 내용

    public ReviewEntity(int id, int movieId, String writer_image, String writer, float rating, String time, long timestamp, int recommend, String contents) {
        this.id = id;
        this.movieId = movieId;
        this.writer_image = writer_image;
        this.writer = writer;
        this.rating = rating;
        this.time = time;
        this.timestamp = timestamp;
        this.recommend = recommend;
        this.contents = contents;
    }

    public int getId() {
        return id;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getWriter_image() {
        return writer_image;
    }

    public String getWriter() {
        if (writer.length() > 1) { // 끝 두자리 * 처리
            return writer.substring(0, writer.length() - 2) + "**";
        } else { // ID 길이가 1
            return "*";
        }
    }

    public float getRating() {
        return rating;
    }

    public String getTime() {
        return time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getRecommend() {
        return recommend;
    }

    public String getContents() {
        return contents;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReviewEntity && this.id == ((ReviewEntity) obj).id;
    }
}
