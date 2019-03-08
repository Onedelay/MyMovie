package com.onedelay.mymovie.data.local.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Movies")
data class MovieEntity(@PrimaryKey val id: Int,
                       val title: String,               // 영화이름
                       val reservation_rate: Float,      // 예매율
                       val reservation_grade: Int,       // 예매순위
                       val grade: Int,                  // 관람등급
                       val date: String,                // 개봉일
                       val genre: String?,               // 장르
                       val duration: Int,               // 러닝타임
                       var like: Int,                   // 좋아요
                       var dislike: Int,                // 싫어요
                       val audience_rating: Float,       // 평점
                       val audience: Int,               // 누적 관객수
                       val synopsis: String?,            // 줄거리
                       val director: String?,            // 감독
                       val actor: String?,               // 출연진
                       val image: String,               // 포스터 URL
                       val thumb: String,               // 썸네일 URL
                       val photos: String?,              // 갤러리 이미지
                       val videos: String?              // 갤러리 동영상
)
