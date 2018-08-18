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
    LiveData<List<MovieEntity>> selectMovies();

    @Query("SELECT * FROM Movies WHERE id = :id")
    MovieEntity selectMovieDetail(int id);

    @Query("DELETE FROM Movies")
    void clear();
}
