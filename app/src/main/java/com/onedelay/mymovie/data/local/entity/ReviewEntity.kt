package com.onedelay.mymovie.data.local.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Reviews")
data class ReviewEntity(
        @PrimaryKey val id: Int,     // 한줄평 ID
        val movieId: Int,            // 영화 ID
        val writer_image: String?,    // 작성자 프로필 이미지
        val writer: String,          // 작성자
        val rating: Float,           // 평점
        val time: String,            // 시간
        val timestamp: Long,         // 시간(unix)
        var recommend: Int,          // 추천수
        val contents: String         // 내용
) {
    fun getHideWriter(): String {
        return if (writer.length > 1) { // 끝 두자리 * 처리
            writer.substring(0, writer.length - 2) + "**"
        } else { // ID 길이가 1
            "*"
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other is ReviewEntity
                && this.id == other.id
                && this.recommend == other.recommend)
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + movieId
        result = 31 * result + writer_image.hashCode()
        result = 31 * result + writer.hashCode()
        result = 31 * result + rating.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + recommend
        result = 31 * result + contents.hashCode()
        return result
    }
}
