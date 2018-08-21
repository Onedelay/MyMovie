package com.onedelay.mymovie.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.onedelay.mymovie.adapter.ReviewAdapter;
import com.onedelay.mymovie.api.GsonRequest;
import com.onedelay.mymovie.api.VolleyHelper;
import com.onedelay.mymovie.api.data.ResponseInfo;
import com.onedelay.mymovie.database.AppDatabase;
import com.onedelay.mymovie.database.ReviewEntity;
import com.onedelay.mymovie.utils.ListDiffCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void requestReviewRecommend(final int reviewId, final String writer){
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/increaseRecommend";

        GsonRequest<ResponseInfo<String>> request = new GsonRequest<ResponseInfo<String>>(Request.Method.POST, url, new TypeToken<ResponseInfo<String>>() {
        }, new Response.Listener<ResponseInfo<String>>() {
            @Override
            public void onResponse(ResponseInfo<String> response) {
                // 성공적으로 추천되었을 경우.
                AppDatabase.getInstance(getApplication()).reviewDao().updateReviewRecommend(reviewId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(), "네트워크 통신 에러", Toast.LENGTH_SHORT).show();
                Log.d(TAG, error.getMessage());
            }
        }){ // Post 방식으로 서버에 요청하는 방법.
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("review_id", String.valueOf(reviewId));
                params.put("writer", writer);

                return params;
            }
        };
        VolleyHelper.requestServer(request);
    }
}
