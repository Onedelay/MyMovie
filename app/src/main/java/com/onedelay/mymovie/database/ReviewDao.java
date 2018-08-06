package com.onedelay.mymovie.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insertReviews(Review... reviews);

    @Update
    void updateReivews(Review... reviews);

    @Query("SELECT * FROM reviews")
    List<Review> selectReviews();
}
