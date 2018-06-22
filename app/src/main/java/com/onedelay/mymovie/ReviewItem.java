package com.onedelay.mymovie;

public class ReviewItem {
    private int image;
    private String id;
    private long time;
    private float rating;
    private String content;
    private String recommend;

    public ReviewItem(int image, String id, long time, float rating, String content, String recommend) {
        this.image = image;
        this.id = id;
        this.time = time;
        this.rating = rating;
        this.content = content;
        this.recommend = recommend;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }
}
