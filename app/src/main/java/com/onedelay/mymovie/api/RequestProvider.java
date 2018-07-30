package com.onedelay.mymovie.api;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestProvider {
    static final String TAG = "Onedelay";

    public static void requestRecommend(final String reviewId, final String writer) {
        String url = "http://" + VolleyHelper.host + ":" + VolleyHelper.port + "/movie/increaseRecommend";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do - nothing
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "##### request error : " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("review_id", reviewId);
                params.put("writer", writer);

                return params;
            }
        };

        VolleyHelper.add(request);
    }
}
