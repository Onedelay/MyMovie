package com.onedelay.mymovie.api.data;

public class MovieInfo {
    private int id;
    private String title;
    private String date;
    private float reservation_rate;
    private int grade;
    private String image;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
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
}
