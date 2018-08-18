package com.onedelay.mymovie.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.onedelay.mymovie.database.AppDatabase;
import com.onedelay.mymovie.database.MovieEntity;

import java.util.List;

public class MovieListViewModel extends AndroidViewModel {
    /**
     * DB 로부터 읽어온 데이터 저장.
     * 여기에 observer 를 달아서 데이터를 관찰하며, 내용이 변경되었을 때 화면 갱신을 하도록 함
     */
    private LiveData<List<MovieEntity>> data;

    public MovieListViewModel(@NonNull Application application) {
        super(application);

        // Room DB 로부터 영화 목록 데이터를 읽어옴.
        data = AppDatabase.getInstance(application.getApplicationContext()).movieDao().selectMovies();
    }

    public LiveData<List<MovieEntity>> getData() {
        return data;
    }
}
