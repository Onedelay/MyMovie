package com.onedelay.mymovie.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insertReviews(ReviewEntity... reviews);

    @Update
    void updateReviews(ReviewEntity... reviews);

    @Query("SELECT * FROM Reviews ORDER BY timestamp DESC")
    LiveData<List<ReviewEntity>> selectReviews();

    @Query("DELETE FROM Reviews")
    void clear();

    @Query("SELECT * FROM Reviews LIMIT 2")
    ReviewEntity[] selectLatestReviews();
}
