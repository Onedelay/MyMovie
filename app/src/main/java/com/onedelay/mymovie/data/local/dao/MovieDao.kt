package com.onedelay.mymovie.data.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.onedelay.mymovie.data.local.entity.MovieEntity

@Dao
interface MovieDao {
    @Insert
    fun insertMovies(vararg movieEntities: MovieEntity)

    @Update
    fun updateMovies(vararg movieEntities: MovieEntity)

    @Query("SELECT * FROM Movies")
    fun selectDataMovies(): List<MovieEntity>

    @Query("SELECT * FROM Movies")
    fun selectMovies(): LiveData<List<MovieEntity>>

    /* Room 에서는 SQL 인젝션를 방지하기 위해 ORDER BY 를 파라미터로 넘길 수 없다.
     * 참고 : https://stackoverflow.com/questions/44240906/android-room-order-by-not-working */
    @Query("SELECT * FROM Movies ORDER BY reservation_rate DESC")
    fun selectMoviesOrderByReservationRate(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM Movies ORDER BY audience_rating DESC")
    fun selectMoviesOrderByAudienceRating(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM Movies ORDER BY date DESC")
    fun selectMoviesOrderByDate(): LiveData<List<MovieEntity>>

    @Query("SELECT * FROM Movies WHERE id = :id")
    fun selectMovieDetailLive(id: Int): LiveData<MovieEntity>

    @Query("SELECT * FROM Movies WHERE id = :id")
    fun selectMovieDetail(id: Int): MovieEntity

    @Query("DELETE FROM Movies")
    fun clear()
}
