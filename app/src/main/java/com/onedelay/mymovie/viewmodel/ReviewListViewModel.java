package com.onedelay.mymovie.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.onedelay.mymovie.database.AppDatabase;
import com.onedelay.mymovie.database.ReviewEntity;

import java.util.List;

public class ReviewListViewModel extends AndroidViewModel {
    private LiveData<List<ReviewEntity>> data;

    public ReviewListViewModel(@NonNull Application application) {
        super(application);
    }

    public void setData(int movieId){
        data = AppDatabase.getInstance(getApplication().getApplicationContext()).reviewDao().selectReviews(movieId);
    }

    public LiveData<List<ReviewEntity>> getData() {
        return data;
    }
}
