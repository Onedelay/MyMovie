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
        if (writer.length() > 1) { // 끝 두자리 * 처리
            return writer.substring(0, writer.length() - 2) + "**";
        } else { // ID 길이가 1
            return "*";
        }
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
        return timestamp * 1000L;
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
