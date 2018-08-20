package com.onedelay.mymovie.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.onedelay.mymovie.api.GsonRequest;
import com.onedelay.mymovie.api.VolleyHelper;
import com.onedelay.mymovie.api.data.ResponseInfo;
import com.onedelay.mymovie.database.AppDatabase;
import com.onedelay.mymovie.database.ReviewEntity;

import java.util.List;

public class ReviewListViewModel extends AndroidViewModel {
    private static final String TAG = "REVIEW_LIST_VIEW_MODEL";

    private LiveData<List<ReviewEntity>> data;

    public ReviewListViewModel(@NonNull Application application) {
        super(application);
    }

    public void setData(int movieId) {
        data = AppDatabase.getInstance(getApplication().getApplicationContext()).reviewDao().selectReviews(movieId);
    }

    public LiveData<List<ReviewEntity>> getData() {
        return data;
    }

    public void requestReviewList(final int movieId) {
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/readCommentList?id=" + movieId;
        GsonRequest<ResponseInfo<List<ReviewEntity>>> request = new GsonRequest<>(Request.Method.GET, url, new TypeToken<ResponseInfo<List<ReviewEntity>>>() {
        }, new Response.Listener<ResponseInfo<List<ReviewEntity>>>() {
            @Override
            public void onResponse(ResponseInfo<List<ReviewEntity>> response) {
                final ReviewEntity[] reviews = new ReviewEntity[response.getResult().size()];
                for (int i = 0; i < response.getResult().size(); i++) {
                    reviews[i] = response.getResult().get(i);
                }
                AppDatabase.getInstance(getApplication()).reviewDao().clear(movieId);
                AppDatabase.getInstance(getApplication()).reviewDao().insertReviews(reviews);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(), "네트워크 통신 에러", Toast.LENGTH_SHORT).show();
                Log.d(TAG, error.getMessage());
            }
        });
        VolleyHelper.requestServer(request);
    }
}
