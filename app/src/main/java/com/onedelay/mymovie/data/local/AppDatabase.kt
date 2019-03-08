package com.onedelay.mymovie.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.onedelay.mymovie.data.local.dao.MovieDao
import com.onedelay.mymovie.data.local.dao.ReviewDao
import com.onedelay.mymovie.data.local.entity.MovieEntity
import com.onedelay.mymovie.data.local.entity.ReviewEntity

@Database(entities = [MovieEntity::class, ReviewEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        private const val DATABASE_NAME = "MyMovie"

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) = INSTANCE
                ?: synchronized(this) {
                    INSTANCE ?: Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build().also { INSTANCE = it }
                }
    }
}
