package com.onedelay.mymovie.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Movies")
public class MovieEntity {
    @PrimaryKey
    private int id;
    private String title;               // 영화이름
    private float reservation_rate;      // 예매율
    private int reservation_grade;       // 예매순위
    private int grade;                  // 관람등급
    private String date;                // 개봉일
    private String genre;               // 장르
    private int duration;               // 러닝타임
    private int like;                   // 좋아요
    private int dislike;                // 싫어요
    private float audience_rating;       // 평점
    private int audience;               // 누적 관객수
    private String synopsis;            // 줄거리
    private String director;            // 감독
    private String actor;               // 출연진
    private String image;               // 포스터 URL
    private String thumb;               // 썸네일 URL
    private String photos;              // 갤러리 이미지
    private String videos;              // 갤러리 동영상

    public MovieEntity(int id, String title, float reservation_rate, int reservation_grade, int grade, String date, String genre, int duration, int like, int dislike, float audience_rating, int audience, String synopsis, String director, String actor, String image, String thumb, String photos, String videos) {
        this.id = id;
        this.title = title;
        this.reservation_rate = reservation_rate;
        this.reservation_grade = reservation_grade;
        this.grade = grade;
        this.date = date;
        this.genre = genre;
        this.duration = duration;
        this.like = like;
        this.dislike = dislike;
        this.audience_rating = audience_rating;
        this.audience = audience;
        this.synopsis = synopsis;
        this.director = director;
        this.actor = actor;
        this.image = image;
        this.thumb = thumb;
        this.photos = photos;
        this.videos = videos;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public float getReservation_rate() {
        return reservation_rate;
    }

    public int getReservation_grade() {
        return reservation_grade;
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

    public float getAudience_rating() {
        return audience_rating;
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

    public String getPhotos() {
        return photos;
    }

    public String getVideos() {
        return videos;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }
}
