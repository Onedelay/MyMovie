package com.onedelay.mymovie.api;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppHelper extends Application{
    public static RequestQueue requestQueue;

    public static String host = "boostcourse-appapi.connect.or.kr";
    public static int port = 10000;

    @Override
    public void onCreate() {
        super.onCreate();
        AppHelper.requestQueue = Volley.newRequestQueue(getBaseContext());
    }
}
