package com.onedelay.mymovie.data.local.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.onedelay.mymovie.data.local.entity.ReviewEntity

@Dao
interface ReviewDao {
    @Insert
    fun insertReviews(vararg reviews: ReviewEntity)

    @Update
    fun updateReviews(vararg reviews: ReviewEntity)

    @Query("UPDATE Reviews SET recommend = recommend + 1 WHERE id = :reviewId")
    fun updateReviewRecommend(reviewId: Int)

    @Query("SELECT * FROM Reviews WHERE movieId = :id ORDER BY timestamp DESC")
    fun selectReviewsLiveData(id: Int): LiveData<List<ReviewEntity>>

    // LiveData 에 데이터가 set 되기 전에 List<ReviewEntity> 를 참조해 NPE 가 발생할 수 있기때문에 추가한 메서드
    @Query("SELECT * FROM Reviews WHERE movieId = :id ORDER BY timestamp DESC")
    fun selectReviews(id: Int): List<ReviewEntity>

    @Query("DELETE FROM Reviews WHERE movieId = :id")
    fun clearReviews(id: Int)

    @Query("SELECT * FROM Reviews LIMIT 2")
    fun selectLatestReviews(): Array<ReviewEntity>
}
