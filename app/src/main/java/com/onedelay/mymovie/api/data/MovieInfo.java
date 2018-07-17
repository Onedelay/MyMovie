package com.onedelay.mymovie.api.data;

public class MovieInfo {
    private int id;
    private String title;
    private String date;
    private String genre;
    private int duration;
    private int like;
    private int dislike;
    private int reservation_grade;
    private float reservation_rate;
    private int grade;
    private String image;
    private String thumb;
    private float user_rating;
    private float audience_rating;
    private float reviewer_rating;
    private int audience;
    private String synopsis;
    private String director;
    private String actor;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public int getLike() {
        return like;
    }

    public int getDislike() {
        return dislike;
    }

    public int getReservation_grade() {
        return reservation_grade;
    }

    public float getReservation_rate() {
        return reservation_rate;
    }

    public int getGrade() {
        return grade;
    }

    public String getImage() {
        return image;
    }

    public String getThumb() {
        return thumb;
    }

    public float getUser_rating() {
        return user_rating;
    }

    public float getAudience_rating() {
        return audience_rating;
    }

    public float getReviewer_rating() {
        return reviewer_rating;
    }

    public int getAudience() {
        return audience;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getDirector() {
        return director;
    }

    public String getActor() {
        return actor;
    }
}
