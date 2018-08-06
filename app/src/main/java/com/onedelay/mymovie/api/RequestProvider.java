package com.onedelay.mymovie.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestProvider {
    static final String TAG = "Onedelay";

    // AllReviewActivity 와 DetailFragment 에서 호출하기 때문에 따로 클래스를 만들어 메소드로 구현하였다.
    public static void requestRecommend(final String reviewId, final String writer, final Runnable runnable) {
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/increaseRecommend";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 서버 응답이 성공적일 경우 run 메소드를 실행하여 화면을 갱신한다.
                        runnable.run();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "##### request error : " + error.getMessage());
                    }
                }
        ) { // Post 방식으로 서버에 요청하는 방법.
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("review_id", reviewId);
                params.put("writer", writer);

                return params;
            }
        };

        VolleyHelper.requestServer(request);
    }

    public static boolean isNetworkConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null;
    }
}
