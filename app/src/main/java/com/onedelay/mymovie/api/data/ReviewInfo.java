package com.onedelay.mymovie.api.data;

public class ReviewInfo {
    private int id;
    private String writer;
    private int movieId;
    private String writer_image;
    private String time;
    private long timestamp;
    private float rating;
    private String contents;
    private int recommend;

    public int getId() {
        return id;
    }

    public String getWriter() {
        return writer;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getWriter_image() {
        return writer_image;
    }

    public String getTime() {
        return time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getRating() {
        return rating;
    }

    public String getContents() {
        return contents;
    }

    public int getRecommend() {
        return recommend;
    }
}
