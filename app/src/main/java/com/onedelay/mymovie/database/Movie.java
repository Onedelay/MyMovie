package com.onedelay.mymovie.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.onedelay.mymovie.api.data.MovieInfo;

@Entity(tableName = "Movies")
public class Movie {
    @PrimaryKey
    private int id;
    private String title;               // 영화이름
    private float reservationRate;      // 예매율
    private int reservationGrade;       // 예매순위
    private int grade;                  // 관람등급
    private String date;                // 개봉일
    private String genre;               // 장르
    private int duration;               // 러닝타임
    private int like;                   // 좋아요
    private int dislike;                // 싫어요
    private float audienceRating;       // 평점
    private int audience;               // 누적 관객수
    private String synopsis;            // 줄거리
    private String director;            // 감독
    private String actor;               // 출연진
    private String image;               // 포스터 URL
    private String thumb;               // 썸네일 URL

    public Movie(int id, String title, float reservationRate, int reservationGrade, int grade, String date, String genre, int duration, int like, int dislike, float audienceRating, int audience, String synopsis, String director, String actor, String image, String thumb) {
        this.id = id;
        this.title = title;
        this.reservationRate = reservationRate;
        this.reservationGrade = reservationGrade;
        this.grade = grade;
        this.date = date;
        this.genre = genre;
        this.duration = duration;
        this.like = like;
        this.dislike = dislike;
        this.audienceRating = audienceRating;
        this.audience = audience;
        this.synopsis = synopsis;
        this.director = director;
        this.actor = actor;
        this.image = image;
        this.thumb = thumb;
    }

    public Movie(MovieInfo movieInfo) {
        this.id = movieInfo.getId();
        this.title = movieInfo.getTitle();
        this.reservationRate = movieInfo.getReservation_rate();
        this.reservationGrade = movieInfo.getReservation_grade();
        this.grade = movieInfo.getGrade();
        this.date = movieInfo.getDate();
        this.genre = movieInfo.getGenre();
        this.duration = movieInfo.getDuration();
        this.like = movieInfo.getLike();
        this.dislike = movieInfo.getDislike();
        this.audienceRating = movieInfo.getAudience_rating();
        this.audience = movieInfo.getAudience();
        this.synopsis = movieInfo.getSynopsis();
        this.director = movieInfo.getDirector();
        this.actor = movieInfo.getActor();
        this.image = movieInfo.getImage();
        this.thumb = movieInfo.getThumb();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public float getReservationRate() {
        return reservationRate;
    }

    public int getReservationGrade() {
        return reservationGrade;
    }

    public int getGrade() {
        return grade;
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

    public float getAudienceRating() {
        return audienceRating;
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

    public String getImage() {
        return image;
    }

    public String getThumb() {
        return thumb;
    }
}
