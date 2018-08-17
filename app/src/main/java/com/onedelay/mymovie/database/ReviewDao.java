package com.onedelay.mymovie.database;

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
    void updateReivews(ReviewEntity... reviews);

    @Query("SELECT * FROM Reviews")
    List<ReviewEntity> selectReviews();
}
