package com.onedelay.mymovie.api;

import com.android.volley.RequestQueue;

public class VolleyHelper {
    public static RequestQueue requestQueue;

    public static String host = "http://boostcourse-appapi.connect.or.kr";
    public static int port = 10000;

    public static void requestServer(GsonRequest request) {
        request.setShouldCache(false);
        if (requestQueue != null) {
            requestQueue.add(request);
        }
    }
}
