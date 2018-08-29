package com.onedelay.mymovie.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.onedelay.mymovie.adapters.ReviewAdapter;
import com.onedelay.mymovie.api.GsonRequest;
import com.onedelay.mymovie.api.VolleyHelper;
import com.onedelay.mymovie.api.data.ResponseInfo;
import com.onedelay.mymovie.database.AppDatabase;
import com.onedelay.mymovie.database.ReviewEntity;

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
        data = AppDatabase.getInstance(getApplication().getApplicationContext()).reviewDao().selectReviewsLiveData(movieId);
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
                //Toast.makeText(getApplication(), "네트워크 통신 에러", Toast.LENGTH_SHORT).show();
                //Log.d(TAG, error.getMessage());
            }
        });
        VolleyHelper.requestServer(request);
    }

    public void requestCreateComment(final int movieId, final String writer, final float rating, final String contents) {
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/createComment";

        GsonRequest<ResponseInfo<String>> request = new GsonRequest<ResponseInfo<String>>(Request.Method.POST, url, new TypeToken<ResponseInfo<String>>() {
        }, new Response.Listener<ResponseInfo<String>>() {
            @Override
            public void onResponse(ResponseInfo<String> response) {
                if (response.getCode() == 200) {
                    //Toast.makeText(getApplication(), "한줄평이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    requestReviewList(movieId);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplication(), "네트워크 통신 에러", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(movieId));
                params.put("writer", writer);
                params.put("rating", String.valueOf(rating));
                params.put("contents", contents);

                return params;
            }
        };
        VolleyHelper.requestServer(request);
    }

    /**
     * @param movieId  영화 ID. 리사이클러뷰 어댑터를 사용하지 않는 경우 필요없음.
     * @param reviewId 한줄평 ID
     * @param writer   한줄평 추천인
     * @param adapter  갱신할 데이터를 가진 리사이클러뷰 어댑터.
     */
    public void requestReviewRecommend(final int movieId, final int reviewId, final String writer, final ReviewAdapter adapter) {
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/increaseRecommend";

        GsonRequest<ResponseInfo<String>> request = new GsonRequest<ResponseInfo<String>>(Request.Method.POST, url, new TypeToken<ResponseInfo<String>>() {
        }, new Response.Listener<ResponseInfo<String>>() {
            @Override
            public void onResponse(ResponseInfo<String> response) {
                // 성공적으로 추천되었을 경우.
                AppDatabase.getInstance(getApplication()).reviewDao().updateReviewRecommend(reviewId);
                if (adapter != null)
                    adapter.updateItem(AppDatabase.getInstance(getApplication()).reviewDao().selectReviews(movieId));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // onResponse 에서 처리 중에 에러가 날 경우 호출됨.
                //Toast.makeText(getApplication(), "네트워크 통신 에러", Toast.LENGTH_SHORT).show();
                //Log.d(TAG, error.getMessage());
            }
        }) { // Post 방식으로 서버에 요청하는 방법.
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
