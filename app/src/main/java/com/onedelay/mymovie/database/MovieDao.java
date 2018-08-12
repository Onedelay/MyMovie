package com.onedelay.mymovie.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert
    void insertMovies(Movie... movies);

    @Update
    void updateMovies(Movie... movies);

    @Query("SELECT * FROM Movies")
    List<Movie> selectMovies();

    @Query("SELECT * FROM Movies WHERE id = :id")
    Movie selectMovieDetail(int id);
}
