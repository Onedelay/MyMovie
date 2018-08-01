package com.onedelay.mymovie.api;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

public class VolleyHelper {
    public static RequestQueue requestQueue;

    public static String host = "boostcourse-appapi.connect.or.kr";
    public static int port = 10000;

    public static void requestServer(StringRequest request) {
        request.setShouldCache(false);
        if (requestQueue != null) {
            requestQueue.add(request);
        }
    }
}
