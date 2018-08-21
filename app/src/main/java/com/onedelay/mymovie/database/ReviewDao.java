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

    @Query("UPDATE Reviews SET recommend = recommend + 1 WHERE id = :reviewId")
    void updateReviewRecommend(int reviewId);

    @Query("SELECT * FROM Reviews WHERE movieId = :id ORDER BY timestamp DESC")
    LiveData<List<ReviewEntity>> selectReviews(int id);

    @Query("DELETE FROM Reviews WHERE movieId = :id")
    void clear(int id);

    @Query("SELECT * FROM Reviews LIMIT 2")
    ReviewEntity[] selectLatestReviews();
}
