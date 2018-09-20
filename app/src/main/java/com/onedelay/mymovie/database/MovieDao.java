package com.onedelay.mymovie.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert
    void insertMovies(MovieEntity... movieEntities);

    @Update
    void updateMovies(MovieEntity... movieEntities);

    @Query("SELECT * FROM Movies")
    List<MovieEntity> selectDataMovies();

    @Query("SELECT * FROM Movies")
    LiveData<List<MovieEntity>> selectMovies();

    /* Room 에서는 SQL 인젝션를 방지하기 위해 ORDER BY 를 파라미터로 넘길 수 없다.
     * 참고 : https://stackoverflow.com/questions/44240906/android-room-order-by-not-working */
    @Query("SELECT * FROM Movies ORDER BY reservation_rate DESC")
    LiveData<List<MovieEntity>> selectMoviesOrderByReservationRate();

    @Query("SELECT * FROM Movies ORDER BY audience_rating DESC")
    LiveData<List<MovieEntity>> selectMoviesOrderByAudienceRating();

    @Query("SELECT * FROM Movies ORDER BY date DESC")
    LiveData<List<MovieEntity>> selectMoviesOrderByDate();

    @Query("SELECT * FROM Movies WHERE id = :id")
    LiveData<MovieEntity> selectMovieDetailLive(int id);

    @Query("SELECT * FROM Movies WHERE id = :id")
    MovieEntity selectMovieDetail(int id);

    @Query("DELETE FROM Movies")
    void clear();
}
